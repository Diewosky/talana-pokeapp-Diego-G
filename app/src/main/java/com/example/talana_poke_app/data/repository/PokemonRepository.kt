package com.example.talana_poke_app.data.repository

// import android.app.Application // No se usa directamente aquí si el DAO es inyectado
import com.example.talana_poke_app.data.local.dao.FavoritePokemonDao
import com.example.talana_poke_app.data.local.entity.FavoritePokemon
import com.example.talana_poke_app.data.model.PokemonDetailResponse
import com.example.talana_poke_app.data.model.PokemonListItem
import com.example.talana_poke_app.data.network.PokeApiService
import com.example.talana_poke_app.data.network.RetrofitInstance
import com.example.talana_poke_app.presentation.pokemonlist.PokemonDisplayItem
import kotlinx.coroutines.flow.Flow

class PokemonRepository(
    private val apiService: PokeApiService = RetrofitInstance.api,
    private val favoritePokemonDao: FavoritePokemonDao // El ViewModel (o DI) lo proveerá
) {

    suspend fun getPokemonList(limit: Int = 20): List<PokemonListItem> {
        return try {
            val response = apiService.getPokemonList(limit = limit)
            response.results
        } catch (e: Exception) {
            // Aquí podrías manejar el error de forma más específica,
            // por ejemplo, loggearlo o lanzar una excepción personalizada.
            emptyList() // Devolvemos una lista vacía en caso de error por ahora.
        }
    }

    suspend fun getPokemonDetail(url: String): PokemonDetailResponse? {
        return try {
            apiService.getPokemonDetail(url)
        } catch (e: Exception) {
            // Loggear error, etc.
            null // Devolver null si falla la obtención del detalle
        }
    }

    // Funciones para Favoritos
    fun isPokemonFavorite(pokemonName: String): Flow<Boolean> {
        return favoritePokemonDao.isFavorite(pokemonName)
    }

    fun getAllFavoritePokemons(): Flow<List<FavoritePokemon>> {
        return favoritePokemonDao.getAllFavorites()
    }

    suspend fun addPokemonToFavorites(pokemon: PokemonDisplayItem) {
        val favorite = FavoritePokemon(
            name = pokemon.name,
            detailUrl = pokemon.detailUrl,
            imageUrl = pokemon.imageUrl
        )
        favoritePokemonDao.addFavorite(favorite)
    }

    suspend fun removePokemonFromFavorites(pokemonName: String) {
        favoritePokemonDao.removeFavorite(pokemonName)
    }
} 