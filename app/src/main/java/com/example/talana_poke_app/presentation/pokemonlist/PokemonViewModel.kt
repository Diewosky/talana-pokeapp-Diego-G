package com.example.talana_poke_app.presentation.pokemonlist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.talana_poke_app.data.local.AppDatabase
import com.example.talana_poke_app.data.model.PokemonListItem
import com.example.talana_poke_app.data.repository.PokemonRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonViewModel(application: Application) : AndroidViewModel(application) {

    private val pokemonRepository: PokemonRepository
    private val _currentListType = MutableStateFlow(PokemonListType.ALL) // Default or initial type

    private val _uiState = MutableStateFlow(PokemonListUiState(isLoading = true))
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        val favoritePokemonDao = database.favoritePokemonDao()
        val pokemonCacheDao = database.pokemonCacheDao()
        
        pokemonRepository = PokemonRepository(
            favoritePokemonDao = favoritePokemonDao,
            pokemonCacheDao = pokemonCacheDao
        )
        observePokemonData()
    }

    fun loadPokemons(listType: PokemonListType) {
        _currentListType.value = listType
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun observePokemonData() {
        viewModelScope.launch {
            val pokemonDataFlow = _currentListType.flatMapLatest { type ->
                _uiState.update { it.copy(isLoading = true, error = null, pokemonList = emptyList()) } // Reset on type change
                when (type) {
                    PokemonListType.ALL -> fetchAllPokemonFromApi()
                    PokemonListType.FAVORITES -> fetchFavoritePokemonDetails()
                }
            }.catch { e ->
                Log.e("PokemonViewModel", "Error in pokemonDataFlow", e)
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error fetching data", pokemonList = emptyList()) }
            }
            .combine(pokemonRepository.getAllFavoritePokemons().catch { emit(emptyList()) }) { displayItems, dbFavorites ->
                val favoriteNames = dbFavorites.map { it.name }.toSet()
                val updatedList = displayItems.map {
                    it.copy(isFavorite = favoriteNames.contains(it.name))
                }
                updatedList
            }
            
            // Combinar con la búsqueda
            pokemonDataFlow.combine(_searchQuery) { pokemonList, searchTerm ->
                if (searchTerm.isBlank()) {
                    pokemonList
                } else {
                    pokemonList.filter { 
                        it.name.contains(searchTerm, ignoreCase = true) 
                    }
                }
            }
            .collect { filteredList ->
                _uiState.update {
                    it.copy(
                        isLoading = false, // Loading is handled before this combine
                        pokemonList = filteredList,
                        error = if (filteredList.isEmpty() && !it.isLoading) {
                            if (_searchQuery.value.isNotBlank()) "No se encontraron resultados para: ${_searchQuery.value}" else "No Pokémon found."
                        } else it.error
                    )
                }
            }
        }
    }

    private fun fetchAllPokemonFromApi(): Flow<List<PokemonDisplayItem>> {
        return flow { 
            val initialList = pokemonRepository.getPokemonList(151)
            
            // Procesar hasta 10 Pokémon a la vez en paralelo
            val pokemonDisplayItems = initialList.chunked(15).flatMap { chunk ->
                chunk.map { pokemonListItem ->
                    viewModelScope.async {
                        mapToDisplayItem(pokemonListItem, pokemonListItem.url)
                    }
                }.awaitAll()
            }
            
            emit(pokemonDisplayItems)
        }
    }

    private fun fetchFavoritePokemonDetails(): Flow<List<PokemonDisplayItem>> {
        return pokemonRepository.getAllFavoritePokemons()
            .map { favoriteList ->
                // Procesar favoritos en paralelo
                favoriteList.map { favoritePokemon ->
                    viewModelScope.async {
                        mapToDisplayItem(
                            PokemonListItem(favoritePokemon.name, favoritePokemon.detailUrl), 
                            favoritePokemon.detailUrl, 
                            favoritePokemon.imageUrl
                        )
                    }
                }.awaitAll()
            }
    }

    private suspend fun mapToDisplayItem(item: PokemonListItem, detailUrl: String, existingImageUrl: String? = null): PokemonDisplayItem {
        val detail = pokemonRepository.getPokemonDetail(detailUrl)
        return PokemonDisplayItem(
            name = item.name,
            imageUrl = existingImageUrl ?: detail?.sprites?.frontDefault,
            detailUrl = detailUrl,
            isFavorite = false,
            id = detail?.id,
            height = detail?.height,
            weight = detail?.weight,
            types = detail?.types?.map { it.type.name.replaceFirstChar(Char::titlecase) },
            abilities = detail?.abilities?.map { it.ability.name.replaceFirstChar(Char::titlecase) },
            stats = detail?.stats?.map { Pair(it.stat.name.replaceFirstChar(Char::titlecase), it.baseStat) }
        )
    }

    fun toggleFavorite(pokemon: PokemonDisplayItem) {
        viewModelScope.launch {
            try {
                if (pokemon.isFavorite) {
                    pokemonRepository.removePokemonFromFavorites(pokemon.name)
                } else {
                    pokemonRepository.addPokemonToFavorites(pokemon)
                }
            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error toggling favorite", e)
                _uiState.update { it.copy(error = "Error updating favorite status") }
            }
        }
    }
} 