package com.example.talana_poke_app.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.talana_poke_app.data.model.UsageStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Repositorio para gestionar las estadísticas de uso de la aplicación
 */
class UsageStatsRepository(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "usage_stats_prefs"
        private const val KEY_USAGE_STATS_PREFIX = "usage_stats_"
        private const val SESSION_START_TIME_PREFIX = "session_start_time_"
        
        @Volatile
        private var INSTANCE: UsageStatsRepository? = null
        
        fun getInstance(context: Context): UsageStatsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UsageStatsRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }
    
    // Estado observable para las estadísticas
    private val _statsFlow = MutableStateFlow(UsageStats())
    val statsFlow: Flow<UsageStats> = _statsFlow.asStateFlow()
    
    // Tiempo de inicio de la sesión actual
    private var sessionStartTime: Long = 0L
    private var currentUserId: String? = null
    
    /**
     * Carga las estadísticas para un usuario específico
     */
    fun loadUserStats(userId: String) {
        currentUserId = userId
        _statsFlow.value = loadStats(userId)
    }
    
    /**
     * Carga las estadísticas desde SharedPreferences para un usuario específico
     */
    private fun loadStats(userId: String): UsageStats {
        val statsJson = sharedPreferences.getString(getStatsKey(userId), null)
        return if (statsJson != null) {
            try {
                json.decodeFromString<UsageStats>(statsJson)
            } catch (e: Exception) {
                UsageStats()
            }
        } else {
            UsageStats()
        }
    }
    
    /**
     * Guarda las estadísticas en SharedPreferences para el usuario actual
     */
    private fun saveStats(stats: UsageStats) {
        currentUserId?.let { userId ->
            val statsJson = json.encodeToString(stats)
            sharedPreferences.edit {
                putString(getStatsKey(userId), statsJson)
            }
            _statsFlow.update { stats }
        }
    }
    
    /**
     * Obtiene la clave para las estadísticas de un usuario
     */
    private fun getStatsKey(userId: String): String = KEY_USAGE_STATS_PREFIX + userId
    
    /**
     * Obtiene la clave para el tiempo de inicio de sesión de un usuario
     */
    private fun getSessionStartTimeKey(userId: String): String = SESSION_START_TIME_PREFIX + userId
    
    /**
     * Inicia una nueva sesión para el usuario actual
     */
    fun startSession() {
        currentUserId?.let { userId ->
            val currentStats = _statsFlow.value
            sessionStartTime = System.currentTimeMillis()
            sharedPreferences.edit {
                putLong(getSessionStartTimeKey(userId), sessionStartTime)
            }
            
            val updatedStats = currentStats.incrementSessionCount()
            saveStats(updatedStats)
        }
    }
    
    /**
     * Finaliza la sesión actual y actualiza el tiempo total
     */
    fun endSession() {
        currentUserId?.let { userId ->
            val currentStats = _statsFlow.value
            val startTime = sharedPreferences.getLong(getSessionStartTimeKey(userId), 0L)
            
            if (startTime > 0) {
                val sessionDuration = System.currentTimeMillis() - startTime
                val updatedStats = currentStats.addActiveTime(sessionDuration)
                saveStats(updatedStats)
            }
        }
    }
    
    /**
     * Incrementa el contador de Pokémon vistos
     */
    fun incrementPokemonViewed() {
        currentUserId?.let {
            val currentStats = _statsFlow.value
            val updatedStats = currentStats.incrementPokemonViewed()
            saveStats(updatedStats)
        }
    }
    
    /**
     * Incrementa el contador de Pokémon favoritos
     */
    fun incrementPokemonFavorited() {
        currentUserId?.let {
            val currentStats = _statsFlow.value
            val updatedStats = currentStats.incrementPokemonFavorited()
            saveStats(updatedStats)
        }
    }
    
    /**
     * Decrementa el contador de Pokémon favoritos
     */
    fun decrementPokemonFavorited() {
        currentUserId?.let {
            val currentStats = _statsFlow.value
            val updatedStats = currentStats.decrementPokemonFavorited()
            saveStats(updatedStats)
        }
    }
    
    /**
     * Sincroniza el contador de Pokémon favoritos con el valor real
     */
    fun syncFavoritesCount(actualCount: Int) {
        currentUserId?.let {
            val currentStats = _statsFlow.value
            val updatedStats = currentStats.copy(
                pokemonFavorited = actualCount,
                lastUpdated = System.currentTimeMillis()
            )
            saveStats(updatedStats)
        }
    }
    
    /**
     * Resetea todas las estadísticas del usuario actual
     */
    fun resetStats() {
        currentUserId?.let {
            saveStats(UsageStats())
        }
    }
    
    /**
     * Limpia las estadísticas del usuario actual
     */
    fun clearCurrentUserStats() {
        currentUserId = null
        _statsFlow.value = UsageStats()
    }
} 