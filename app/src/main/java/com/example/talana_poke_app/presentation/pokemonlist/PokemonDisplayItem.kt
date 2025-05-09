package com.example.talana_poke_app.presentation.pokemonlist

data class PokemonDisplayItem(
    val name: String,
    val imageUrl: String?,
    val detailUrl: String // Guardamos la URL de detalle por si la necesitamos luego
) 