package com.example.talana_poke_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.talana_poke_app.data.local.converter.PokemonTypeConverter
import java.util.Date

@Entity(tableName = "pokemon_cache")
@TypeConverters(PokemonTypeConverter::class)
data class PokemonCache(
    @PrimaryKey
    val name: String,
    val id: Int?,
    val imageUrl: String?,
    val detailUrl: String,
    val height: Int?,
    val weight: Int?,
    val types: List<String>?,
    val abilities: List<String>?,
    val stats: Map<String, Int>?,
    val timestamp: Date = Date() // Para control de obsolescencia de caché
) 