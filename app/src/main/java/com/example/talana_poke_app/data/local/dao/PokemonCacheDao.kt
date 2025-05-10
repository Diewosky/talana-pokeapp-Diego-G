package com.example.talana_poke_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.talana_poke_app.data.local.entity.PokemonCache
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: PokemonCache)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemons: List<PokemonCache>)

    @Query("SELECT * FROM pokemon_cache WHERE name = :name")
    suspend fun getPokemonByName(name: String): PokemonCache?

    @Query("SELECT * FROM pokemon_cache")
    suspend fun getAllPokemon(): List<PokemonCache>

    @Query("SELECT * FROM pokemon_cache")
    fun getAllPokemonFlow(): Flow<List<PokemonCache>>

    @Query("DELETE FROM pokemon_cache WHERE name = :name")
    suspend fun deletePokemon(name: String)

    @Query("DELETE FROM pokemon_cache")
    suspend fun clearCache()

    @Query("SELECT COUNT(*) FROM pokemon_cache")
    suspend fun getCacheSize(): Int
} 