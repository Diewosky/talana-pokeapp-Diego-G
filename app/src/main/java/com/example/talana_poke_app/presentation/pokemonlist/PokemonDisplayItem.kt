package com.example.talana_poke_app.presentation.pokemonlist

data class PokemonDisplayItem(
    val name: String,
    val imageUrl: String?,
    val detailUrl: String, // Guardamos la URL de detalle por si la necesitamos luego
    val isFavorite: Boolean = false, // Nuevo campo
    // Nuevos campos para detalles
    val id: Int? = null,
    val height: Int? = null,         // en decímetros
    val weight: Int? = null,         // en hectogramos
    val types: List<String>? = null, // Lista de nombres de tipos
    val abilities: List<String>? = null, // Lista de nombres de habilidades
    val stats: List<Pair<String, Int>>? = null // Lista de pares (nombre de stat, valor base)
) 