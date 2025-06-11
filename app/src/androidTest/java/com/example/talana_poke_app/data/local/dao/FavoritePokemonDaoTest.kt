package com.example.talana_poke_app.data.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.talana_poke_app.data.local.AppDatabase
import com.example.talana_poke_app.data.local.entity.FavoritePokemon
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FavoritePokemonDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: FavoritePokemonDao

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.favoritePokemonDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetFavoritePokemon() = runTest {
        // Given
        val favoritePokemon = FavoritePokemon(
            userId = "user123",
            name = "pikachu",
            detailUrl = "https://pokeapi.co/api/v2/pokemon/25/",
            imageUrl = "https://example.com/pikachu.png"
        )

        // When
        dao.addFavorite(favoritePokemon)

        // Then
        dao.getAllFavorites("user123").test {
            val favorites = awaitItem()
            assertThat(favorites).hasSize(1)
            assertThat(favorites[0].name).isEqualTo("pikachu")
            assertThat(favorites[0].userId).isEqualTo("user123")
        }
    }

    @Test
    fun isFavoriteReturnsTrueForFavoritePokemon() = runTest {
        // Given
        val favoritePokemon = FavoritePokemon(
            userId = "user123",
            name = "pikachu",
            detailUrl = "https://pokeapi.co/api/v2/pokemon/25/",
            imageUrl = "https://example.com/pikachu.png"
        )
        dao.addFavorite(favoritePokemon)

        // When/Then
        dao.isFavorite("user123", "pikachu").test {
            assertThat(awaitItem()).isTrue()
        }
    }

    @Test
    fun isFavoriteReturnsFalseForNonFavoritePokemon() = runTest {
        // When/Then
        dao.isFavorite("user123", "pikachu").test {
            assertThat(awaitItem()).isFalse()
        }
    }

    @Test
    fun removeFavoriteDeletesPokemon() = runTest {
        // Given
        val favoritePokemon = FavoritePokemon(
            userId = "user123",
            name = "pikachu",
            detailUrl = "https://pokeapi.co/api/v2/pokemon/25/",
            imageUrl = "https://example.com/pikachu.png"
        )
        dao.addFavorite(favoritePokemon)

        // When
        dao.removeFavorite("user123", "pikachu")

        // Then
        dao.getAllFavorites("user123").test {
            val favorites = awaitItem()
            assertThat(favorites).isEmpty()
        }
    }

    @Test
    fun getFavoritesFiltersByUserId() = runTest {
        // Given
        val user1Pokemon = FavoritePokemon(
            userId = "user1",
            name = "pikachu",
            detailUrl = "https://pokeapi.co/api/v2/pokemon/25/",
            imageUrl = "https://example.com/pikachu.png"
        )
        val user2Pokemon = FavoritePokemon(
            userId = "user2",
            name = "charmander",
            detailUrl = "https://pokeapi.co/api/v2/pokemon/4/",
            imageUrl = "https://example.com/charmander.png"
        )
        dao.addFavorite(user1Pokemon)
        dao.addFavorite(user2Pokemon)

        // When/Then
        dao.getAllFavorites("user1").test {
            val favorites = awaitItem()
            assertThat(favorites).hasSize(1)
            assertThat(favorites[0].name).isEqualTo("pikachu")
            assertThat(favorites[0].userId).isEqualTo("user1")
        }

        dao.getAllFavorites("user2").test {
            val favorites = awaitItem()
            assertThat(favorites).hasSize(1)
            assertThat(favorites[0].name).isEqualTo("charmander")
            assertThat(favorites[0].userId).isEqualTo("user2")
        }
    }

    @Test
    fun clearUserFavoritesRemovesAllFavoritesForUser() = runTest {
        // Given
        val user1Pokemon1 = FavoritePokemon(
            userId = "user1",
            name = "pikachu",
            detailUrl = "https://pokeapi.co/api/v2/pokemon/25/",
            imageUrl = "https://example.com/pikachu.png"
        )
        val user1Pokemon2 = FavoritePokemon(
            userId = "user1",
            name = "charmander",
            detailUrl = "https://pokeapi.co/api/v2/pokemon/4/",
            imageUrl = "https://example.com/charmander.png"
        )
        val user2Pokemon = FavoritePokemon(
            userId = "user2",
            name = "bulbasaur",
            detailUrl = "https://pokeapi.co/api/v2/pokemon/1/",
            imageUrl = "https://example.com/bulbasaur.png"
        )
        
        dao.addFavorite(user1Pokemon1)
        dao.addFavorite(user1Pokemon2)
        dao.addFavorite(user2Pokemon)

        // When
        dao.clearUserFavorites("user1")

        // Then
        dao.getAllFavorites("user1").test {
            val user1Favorites = awaitItem()
            assertThat(user1Favorites).isEmpty()
        }

        dao.getAllFavorites("user2").test {
            val user2Favorites = awaitItem()
            assertThat(user2Favorites).hasSize(1)
            assertThat(user2Favorites[0].name).isEqualTo("bulbasaur")
        }
    }

    @Test
    fun insertDuplicateFavoriteReplacesExisting() = runTest {
        // Given
        val originalFavorite = FavoritePokemon(
            userId = "user123",
            name = "pikachu",
            detailUrl = "https://pokeapi.co/api/v2/pokemon/25/",
            imageUrl = "https://example.com/pikachu_old.png"
        )
        val updatedFavorite = FavoritePokemon(
            userId = "user123",
            name = "pikachu",
            detailUrl = "https://pokeapi.co/api/v2/pokemon/25/",
            imageUrl = "https://example.com/pikachu_new.png"
        )

        // When
        dao.addFavorite(originalFavorite)
        dao.addFavorite(updatedFavorite) // Debería reemplazar por la estrategia REPLACE

        // Then
        dao.getAllFavorites("user123").test {
            val favorites = awaitItem()
            assertThat(favorites).hasSize(1)
            assertThat(favorites[0].imageUrl).isEqualTo("https://example.com/pikachu_new.png")
        }
    }
} 