package com.example.talana_poke_app.data.model

import com.google.gson.annotations.SerializedName
 
data class PokemonSprites(
    @SerializedName("front_default") val frontDefault: String?
    // Puedes añadir otros sprites aquí si los necesitas (e.g., front_shiny, back_default, etc.)
) 