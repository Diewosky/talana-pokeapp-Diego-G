package com.example.talana_poke_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_pokemon_table")
data class FavoritePokemon(
    @PrimaryKey val name: String, // Usaremos el nombre como clave primaria
    val detailUrl: String,
    val imageUrl: String?
) 