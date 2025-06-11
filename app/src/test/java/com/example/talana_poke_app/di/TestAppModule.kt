package com.example.talana_poke_app.di

import com.example.talana_poke_app.data.local.dao.FavoritePokemonDao
import com.example.talana_poke_app.data.local.dao.PokemonCacheDao
import com.example.talana_poke_app.data.network.PokeApiService
import com.example.talana_poke_app.data.repository.PokemonRepository
import com.example.talana_poke_app.data.repository.UsageStatsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

/**
 * Módulo de Hilt para tests que reemplaza AppModule
 * Proporciona mocks en lugar de implementaciones reales
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    @Provides
    @Singleton
    fun provideMockPokeApiService(): PokeApiService = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideMockFavoritePokemonDao(): FavoritePokemonDao = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideMockPokemonCacheDao(): PokemonCacheDao = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideMockPokemonRepository(): PokemonRepository = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideMockUsageStatsRepository(): UsageStatsRepository = mockk(relaxed = true)
} 