package com.example.talana_poke_app.data.model

import com.google.gson.annotations.SerializedName

// Contenedor para la información del tipo dentro de la lista de tipos de un Pokémon
data class PokemonTypeHolder(
    @SerializedName("slot") val slot: Int,
    @SerializedName("type") val type: PokemonTypeInfo
)

// Información específica del tipo
data class PokemonTypeInfo(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String // URL para obtener más detalles sobre este tipo si fuera necesario
)