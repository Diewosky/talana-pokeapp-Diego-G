package com.example.talana_poke_app.di

import android.content.Context
import com.example.talana_poke_app.data.local.AppDatabase
import com.example.talana_poke_app.data.local.dao.FavoritePokemonDao
import com.example.talana_poke_app.data.local.dao.PokemonCacheDao
import com.example.talana_poke_app.data.network.PokeApiService
import com.example.talana_poke_app.data.network.RetrofitInstance
import com.example.talana_poke_app.data.repository.PokemonRepository
import com.example.talana_poke_app.data.repository.UsageStatsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideFavoritePokemonDao(database: AppDatabase): FavoritePokemonDao {
        return database.favoritePokemonDao()
    }

    @Provides
    fun providePokemonCacheDao(database: AppDatabase): PokemonCacheDao {
        return database.pokemonCacheDao()
    }

    @Provides
    @Singleton
    fun providePokeApiService(): PokeApiService {
        return RetrofitInstance.api
    }

    @Provides
    @Singleton
    fun providePokemonRepository(
        apiService: PokeApiService,
        favoritePokemonDao: FavoritePokemonDao,
        pokemonCacheDao: PokemonCacheDao
    ): PokemonRepository {
        return PokemonRepository(
            apiService = apiService,
            favoritePokemonDao = favoritePokemonDao,
            pokemonCacheDao = pokemonCacheDao
        )
    }

    @Provides
    @Singleton
    fun provideUsageStatsRepository(@ApplicationContext context: Context): UsageStatsRepository {
        return UsageStatsRepository.getInstance(context)
    }
} 