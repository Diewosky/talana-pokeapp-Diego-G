package com.example.talana_poke_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "favorite_pokemon_table",
    primaryKeys = ["userId", "name"],
    indices = [Index(value = ["userId"])]
)
data class FavoritePokemon(
    val userId: String,
    val name: String, // Usaremos userId+name como clave primaria compuesta
    val detailUrl: String,
    val imageUrl: String?
) 