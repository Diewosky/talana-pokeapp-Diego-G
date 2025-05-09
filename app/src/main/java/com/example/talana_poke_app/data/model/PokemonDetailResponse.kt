package com.example.talana_poke_app.data.model

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val sprites: PokemonSprites
    // Puedes añadir otras propiedades del detalle que necesites (height, weight, types, etc.)
) 