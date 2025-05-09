package com.example.talana_poke_app.data.model

// No necesitamos importar SerializedName aquí si no se usa directamente en este archivo

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val sprites: PokemonSprites,
    val height: Int, // Altura en decímetros
    val weight: Int, // Peso en hectogramos
    val types: List<PokemonTypeHolder>,
    val abilities: List<PokemonAbilityHolder>,
    val stats: List<PokemonStatHolder>
    // Puedes añadir otras propiedades del detalle que necesites (height, weight, types, etc.)
) 