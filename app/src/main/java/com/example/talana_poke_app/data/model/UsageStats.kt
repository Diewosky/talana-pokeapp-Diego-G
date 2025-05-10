package com.example.talana_poke_app.data.model

import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

/**
 * Modelo de datos para estadísticas de uso de la aplicación
 */
@Serializable
data class UsageStats(
    // Contador de Pokémon vistos en total (visualizados en detalle)
    val pokemonViewed: Int = 0,
    
    // Contador de Pokémon marcados como favoritos en total
    val pokemonFavorited: Int = 0,
    
    // Contador de sesiones de la aplicación
    val sessionCount: Int = 0,
    
    // Tiempo total activo en la aplicación (en milisegundos)
    val totalTimeActive: Long = 0L,
    
    // Última vez que se actualizaron las estadísticas
    val lastUpdated: Long = System.currentTimeMillis()
) {
    /**
     * Formatea el tiempo total activo en un formato legible
     */
    fun getFormattedTotalTime(): String {
        val hours = TimeUnit.MILLISECONDS.toHours(totalTimeActive)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(totalTimeActive) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(totalTimeActive) % 60
        
        return when {
            hours > 0 -> String.format("%d h %d min %d s", hours, minutes, seconds)
            minutes > 0 -> String.format("%d min %d s", minutes, seconds)
            else -> String.format("%d s", seconds)
        }
    }
    
    /**
     * Incrementa el contador de Pokémon vistos
     */
    fun incrementPokemonViewed(): UsageStats {
        return this.copy(
            pokemonViewed = pokemonViewed + 1,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Incrementa el contador de Pokémon marcados como favoritos
     */
    fun incrementPokemonFavorited(): UsageStats {
        return this.copy(
            pokemonFavorited = pokemonFavorited + 1,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Decrementa el contador de Pokémon marcados como favoritos
     */
    fun decrementPokemonFavorited(): UsageStats {
        return this.copy(
            pokemonFavorited = maxOf(0, pokemonFavorited - 1),
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Incrementa el contador de sesiones
     */
    fun incrementSessionCount(): UsageStats {
        return this.copy(
            sessionCount = sessionCount + 1,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Añade tiempo activo a las estadísticas
     */
    fun addActiveTime(timeInMillis: Long): UsageStats {
        return this.copy(
            totalTimeActive = totalTimeActive + timeInMillis,
            lastUpdated = System.currentTimeMillis()
        )
    }
} 