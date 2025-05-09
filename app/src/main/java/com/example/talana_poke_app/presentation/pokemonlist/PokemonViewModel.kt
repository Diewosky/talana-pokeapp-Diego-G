package com.example.talana_poke_app.presentation.pokemonlist

import android.util.Log // Para logging de ejemplo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.talana_poke_app.data.repository.PokemonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PokemonViewModel(
    private val pokemonRepository: PokemonRepository = PokemonRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonListUiState())
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    init {
        fetchPokemonList()
    }

    fun fetchPokemonList(limit: Int = 20) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val initialList = pokemonRepository.getPokemonList(limit)
                if (initialList.isEmpty() && limit > 0) { // Chequeo adicional si la lista inicial falla
                    _uiState.update { it.copy(isLoading = false, error = "Failed to load initial Pokémon list.") }
                    return@launch
                }

                // Usamos async y awaitAll para obtener detalles en paralelo
                val detailedPokemonList = initialList.map { pokemonListItem ->
                    async { // Lanza cada llamada de detalle en una corrutina separada
                        val detail = pokemonRepository.getPokemonDetail(pokemonListItem.url)
                        PokemonDisplayItem(
                            name = pokemonListItem.name,
                            imageUrl = detail?.sprites?.frontDefault,
                            detailUrl = pokemonListItem.url
                        )
                    }
                }.awaitAll() // Espera a que todas las llamadas de detalle terminen

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        pokemonList = detailedPokemonList
                    )
                }

            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error fetching Pokémon list or details", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = e.message ?: "An unknown error occurred fetching details"
                    )
                }
            }
        }
    }
} 