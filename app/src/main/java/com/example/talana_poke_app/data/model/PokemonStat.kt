package com.example.talana_poke_app.data.model

import com.google.gson.annotations.SerializedName

// Contenedor para la información de la estadística dentro de la lista de estadísticas de un Pokémon
data class PokemonStatHolder(
    @SerializedName("base_stat") val baseStat: Int,
    @SerializedName("effort") val effort: Int,
    @SerializedName("stat") val stat: PokemonStatInfo
)

// Información específica de la estadística
data class PokemonStatInfo(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String // URL para obtener más detalles sobre esta estadística si fuera necesario
)