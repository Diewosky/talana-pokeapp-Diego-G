package com.example.talana_poke_app.presentation.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.talana_poke_app.data.model.UsageStats
import com.example.talana_poke_app.data.repository.UsageStatsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar las estadísticas de uso de la aplicación
 */
class UsageStatsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val usageStatsRepository = UsageStatsRepository.getInstance(application)
    
    val usageStats: StateFlow<UsageStats> = usageStatsRepository.statsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UsageStats()
        )
    
    /**
     * Carga las estadísticas para un usuario específico
     */
    fun loadUserStats(userId: String) {
        usageStatsRepository.loadUserStats(userId)
        startSession()
    }
    
    /**
     * Limpia las estadísticas del usuario actual
     */
    fun clearCurrentUserStats() {
        usageStatsRepository.clearCurrentUserStats()
    }
    
    /**
     * Inicia una nueva sesión
     */
    fun startSession() {
        viewModelScope.launch {
            usageStatsRepository.startSession()
        }
    }
    
    /**
     * Finaliza la sesión actual
     */
    fun endSession() {
        viewModelScope.launch {
            usageStatsRepository.endSession()
        }
    }
    
    /**
     * Registra que se ha visto un Pokémon
     */
    fun onPokemonViewed() {
        viewModelScope.launch {
            usageStatsRepository.incrementPokemonViewed()
        }
    }
    
    /**
     * Registra que se ha marcado un Pokémon como favorito
     */
    fun onPokemonFavorited() {
        viewModelScope.launch {
            usageStatsRepository.incrementPokemonFavorited()
        }
    }
    
    /**
     * Registra que se ha desmarcado un Pokémon como favorito
     */
    fun onPokemonUnfavorited() {
        viewModelScope.launch {
            usageStatsRepository.decrementPokemonFavorited()
        }
    }
    
    /**
     * Sincroniza el contador de favoritos con el número real
     */
    fun syncFavoritesCount(actualCount: Int) {
        viewModelScope.launch {
            usageStatsRepository.syncFavoritesCount(actualCount)
        }
    }
    
    /**
     * Resetea todas las estadísticas
     */
    fun resetStats() {
        viewModelScope.launch {
            usageStatsRepository.resetStats()
        }
    }
    
    override fun onCleared() {
        // Finalizar la sesión cuando se destruye el ViewModel
        endSession()
        super.onCleared()
    }
} 