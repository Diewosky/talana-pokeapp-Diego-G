package com.example.talana_poke_app.data.repository

import com.example.talana_poke_app.data.model.PokemonListItem
import com.example.talana_poke_app.data.model.PokemonDetailResponse
import com.example.talana_poke_app.data.network.PokeApiService
import com.example.talana_poke_app.data.network.RetrofitInstance

class PokemonRepository(private val apiService: PokeApiService = RetrofitInstance.api) {

    suspend fun getPokemonList(limit: Int = 20): List<PokemonListItem> {
        return try {
            val response = apiService.getPokemonList(limit = limit)
            response.results
        } catch (e: Exception) {
            // Aquí podrías manejar el error de forma más específica,
            // por ejemplo, loggearlo o lanzar una excepción personalizada.
            emptyList() // Devolvemos una lista vacía en caso de error por ahora.
        }
    }

    suspend fun getPokemonDetail(url: String): PokemonDetailResponse? {
        return try {
            apiService.getPokemonDetail(url)
        } catch (e: Exception) {
            // Loggear error, etc.
            null // Devolver null si falla la obtención del detalle
        }
    }
} 