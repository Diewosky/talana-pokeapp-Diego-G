package com.example.talana_poke_app.presentation.pokemonlist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.talana_poke_app.data.local.AppDatabase
import com.example.talana_poke_app.data.repository.PokemonRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonViewModel(application: Application) : AndroidViewModel(application) {

    private val pokemonRepository: PokemonRepository
    private val _triggerFetch = MutableStateFlow(Unit) // Para re-disparar la carga si es necesario

    init {
        val favoritePokemonDao = AppDatabase.getDatabase(application).favoritePokemonDao()
        pokemonRepository = PokemonRepository(favoritePokemonDao = favoritePokemonDao)
    }

    private val _rawPokemonListFromApi = _triggerFetch.flatMapLatest {
        _uiState.update { it.copy(isLoading = true, error = null) } // Indicar carga al inicio del fetch
        flowOf(pokemonRepository.getPokemonList(20))
            .map { initialList ->
                // En una app real, las llamadas de detalle podrían ser más eficientes o diferidas
                initialList.map { pokemonListItem ->
                    val detail = pokemonRepository.getPokemonDetail(pokemonListItem.url)
                    PokemonDisplayItem(
                        name = pokemonListItem.name,
                        imageUrl = detail?.sprites?.frontDefault,
                        detailUrl = pokemonListItem.url,
                        isFavorite = false // Se actualizará con el combine
                    )
                }
            }
            .catch { e ->
                Log.e("PokemonViewModel", "Error fetching initial list/details", e)
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error fetching data") }
                emit(emptyList()) 
            }
    }

    private val _favoritePokemons = pokemonRepository.getAllFavoritePokemons()
        .catch { e ->
            Log.e("PokemonViewModel", "Error fetching favorites", e)
            emit(emptyList()) 
        }

    private val _uiState = MutableStateFlow(PokemonListUiState(isLoading = true))
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                _rawPokemonListFromApi,
                _favoritePokemons
            ) { pokemonFromApi, favoritesFromDb ->
                val favoriteNames = favoritesFromDb.map { it.name }.toSet()
                val updatedList = pokemonFromApi.map { displayItem ->
                    displayItem.copy(isFavorite = favoriteNames.contains(displayItem.name))
                }
                // Solo actualiza isLoading a false si realmente tenemos una lista (o un error ya fue seteado)
                val currentError = _uiState.value.error
                val isLoading = pokemonFromApi.isEmpty() && currentError == null // Sigue cargando si la API no devolvió nada y no hay error previo
                
                Pair(updatedList, isLoading)

            }.collect { (combinedList, stillLoading) -> 
                _uiState.update {
                    it.copy(
                        isLoading = stillLoading,
                        pokemonList = combinedList,
                        // Mantener error si ya existía, o poner uno nuevo si la lista está vacía y no hay error
                        error = if (combinedList.isEmpty() && !stillLoading && it.error == null) "No Pokémon found." else it.error
                    )
                }
            }
        }
         _triggerFetch.value = Unit // Inicia la carga explícitamente después de configurar el colector
    }


    fun toggleFavorite(pokemon: PokemonDisplayItem) {
        viewModelScope.launch {
            try {
                if (pokemon.isFavorite) {
                    pokemonRepository.removePokemonFromFavorites(pokemon.name)
                } else {
                    pokemonRepository.addPokemonToFavorites(pokemon.copy(isFavorite = true)) // Guardamos con isFavorite = true por consistencia, aunque la BD no lo use
                }
            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error toggling favorite", e)
                // Considerar actualizar uiState con un error específico del toggle
            }
        }
    }
} 