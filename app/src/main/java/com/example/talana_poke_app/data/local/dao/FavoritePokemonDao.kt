package com.example.talana_poke_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.talana_poke_app.data.local.entity.FavoritePokemon
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favoritePokemon: FavoritePokemon)

    @Query("DELETE FROM favorite_pokemon_table WHERE name = :pokemonName")
    suspend fun removeFavorite(pokemonName: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_pokemon_table WHERE name = :pokemonName LIMIT 1)")
    fun isFavorite(pokemonName: String): Flow<Boolean>

    @Query("SELECT * FROM favorite_pokemon_table ORDER BY name ASC")
    fun getAllFavorites(): Flow<List<FavoritePokemon>>
} 