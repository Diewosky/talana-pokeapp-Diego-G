package com.example.talana_poke_app.presentation.pokemonlist

// import com.example.talana_poke_app.data.model.PokemonListItem // Ya no se usa aquí directamente
 
data class PokemonListUiState(
    val isLoading: Boolean = false,
    val pokemonList: List<PokemonDisplayItem> = emptyList(), // Cambiado a PokemonDisplayItem
    val error: String? = null
) 