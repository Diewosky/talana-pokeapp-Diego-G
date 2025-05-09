package com.example.talana_poke_app.data.model

import com.google.gson.annotations.SerializedName

// Contenedor para la información de la habilidad dentro de la lista de habilidades de un Pokémon
data class PokemonAbilityHolder(
    @SerializedName("ability") val ability: PokemonAbilityInfo,
    @SerializedName("is_hidden") val isHidden: Boolean,
    @SerializedName("slot") val slot: Int
)

// Información específica de la habilidad
data class PokemonAbilityInfo(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String // URL para obtener más detalles sobre esta habilidad si fuera necesario
)