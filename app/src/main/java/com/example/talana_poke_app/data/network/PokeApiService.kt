package com.example.talana_poke_app.data.network

import com.example.talana_poke_app.data.model.PokemonListResponse
import com.example.talana_poke_app.data.model.PokemonDetailResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemonList(@Query("limit") limit: Int = 20): PokemonListResponse

    @GET
    suspend fun getPokemonDetail(@Url url: String): PokemonDetailResponse
} 