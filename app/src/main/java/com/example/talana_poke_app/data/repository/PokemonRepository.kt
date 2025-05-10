package com.example.talana_poke_app.data.repository

import android.util.Log
import com.example.talana_poke_app.data.local.dao.FavoritePokemonDao
import com.example.talana_poke_app.data.local.dao.PokemonCacheDao
import com.example.talana_poke_app.data.local.entity.FavoritePokemon
import com.example.talana_poke_app.data.local.entity.PokemonCache
import com.example.talana_poke_app.data.model.*
import com.example.talana_poke_app.data.network.PokeApiService
import com.example.talana_poke_app.data.network.RetrofitInstance
import com.example.talana_poke_app.presentation.pokemonlist.PokemonDisplayItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.concurrent.TimeUnit

class PokemonRepository(
    private val apiService: PokeApiService = RetrofitInstance.api,
    private val favoritePokemonDao: FavoritePokemonDao,
    private val pokemonCacheDao: PokemonCacheDao? = null // Opcional para compatibilidad con código existente
) {
    // Tiempo de expiración de caché: 24 horas
    private val cacheExpirationTime = TimeUnit.HOURS.toMillis(24)

    suspend fun getPokemonList(limit: Int = 20): List<PokemonListItem> {
        return try {
            val response = apiService.getPokemonList(limit = limit)
            response.results
        } catch (e: Exception) {
            Log.e("PokemonRepository", "Error fetching Pokemon list", e)
            emptyList()
        }
    }

    suspend fun getPokemonDetail(url: String): PokemonDetailResponse? {
        // Extraer el nombre del Pokémon de la URL
        val pokemonName = url.split("/").filter { it.isNotEmpty() }.lastOrNull()
        
        if (pokemonName != null) {
            // Verificar si tenemos el Pokémon en caché
            pokemonCacheDao?.let { cacheDao ->
                val cachedPokemon = cacheDao.getPokemonByName(pokemonName)
                
                // Si existe en caché y no ha expirado, convertirlo a PokemonDetailResponse y retornarlo
                if (cachedPokemon != null && !isCacheExpired(cachedPokemon.timestamp)) {
                    Log.d("PokemonRepository", "Cache hit for $pokemonName")
                    return mapCacheToPokemonDetail(cachedPokemon)
                }
            }
        }
        
        // Si no está en caché o expiró, obtenerlo de la API
        return try {
            val detailResponse = apiService.getPokemonDetail(url)
            
            // Guardar en caché si el DAO está disponible
            if (pokemonName != null) {
                saveToCache(detailResponse, url)
            }
            
            detailResponse
        } catch (e: Exception) {
            Log.e("PokemonRepository", "Error fetching Pokemon detail", e)
            null
        }
    }

    // Funciones para manejar la caché
    private suspend fun saveToCache(detailResponse: PokemonDetailResponse, url: String) {
        pokemonCacheDao?.let { cacheDao ->
            try {
                val pokemonCache = PokemonCache(
                    name = detailResponse.name,
                    id = detailResponse.id,
                    imageUrl = detailResponse.sprites.frontDefault,
                    detailUrl = url,
                    height = detailResponse.height,
                    weight = detailResponse.weight,
                    types = detailResponse.types?.map { it.type.name },
                    abilities = detailResponse.abilities?.map { it.ability.name },
                    stats = detailResponse.stats?.associate { it.stat.name to it.baseStat },
                    timestamp = Date()
                )
                withContext(Dispatchers.IO) {
                    cacheDao.insertPokemon(pokemonCache)
                }
                Log.d("PokemonRepository", "Saved ${detailResponse.name} to cache")
            } catch (e: Exception) {
                Log.e("PokemonRepository", "Error saving to cache", e)
            }
        }
    }

    private fun isCacheExpired(timestamp: Date): Boolean {
        val currentTime = System.currentTimeMillis()
        val cacheTime = timestamp.time
        return (currentTime - cacheTime) > cacheExpirationTime
    }

    private fun mapCacheToPokemonDetail(cache: PokemonCache): PokemonDetailResponse {
        // Crear instancias de las clases anidadas que necesita PokemonDetailResponse
        val sprites = PokemonSprites(cache.imageUrl)
        
        val types = cache.types?.map { typeName ->
            PokemonTypeHolder(
                slot = 0, // Valor por defecto
                type = PokemonTypeInfo(name = typeName, url = "")
            )
        }
        
        val abilities = cache.abilities?.map { abilityName ->
            PokemonAbilityHolder(
                ability = PokemonAbilityInfo(name = abilityName, url = ""),
                isHidden = false, // Valor por defecto
                slot = 0 // Valor por defecto
            )
        }
        
        val stats = cache.stats?.map { (statName, value) ->
            PokemonStatHolder(
                baseStat = value,
                effort = 0, // Valor por defecto
                stat = PokemonStatInfo(name = statName, url = "")
            )
        }
        
        return PokemonDetailResponse(
            id = cache.id ?: 0,
            name = cache.name,
            height = cache.height ?: 0,
            weight = cache.weight ?: 0,
            sprites = sprites,
            types = types ?: emptyList(),
            abilities = abilities ?: emptyList(),
            stats = stats ?: emptyList()
        )
    }

    // Funciones para Favoritos
    fun isPokemonFavorite(userId: String, pokemonName: String): Flow<Boolean> {
        return favoritePokemonDao.isFavorite(userId, pokemonName)
    }

    fun getAllFavoritePokemons(userId: String): Flow<List<FavoritePokemon>> {
        return favoritePokemonDao.getAllFavorites(userId)
    }

    suspend fun addPokemonToFavorites(userId: String, pokemon: PokemonDisplayItem) {
        val favorite = FavoritePokemon(
            userId = userId,
            name = pokemon.name,
            detailUrl = pokemon.detailUrl,
            imageUrl = pokemon.imageUrl
        )
        favoritePokemonDao.addFavorite(favorite)
    }

    suspend fun removePokemonFromFavorites(userId: String, pokemonName: String) {
        favoritePokemonDao.removeFavorite(userId, pokemonName)
    }
} 