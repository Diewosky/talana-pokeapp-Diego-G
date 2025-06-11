package com.example.talana_poke_app

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.talana_poke_app.data.model.*
import com.example.talana_poke_app.presentation.pokemonlist.PokemonDisplayItem
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UtilityFunctionsTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun `URL parsing extracts Pokemon ID correctly`() = runTest(testDispatcher) {
        // Given
        val testUrls = mapOf(
            "https://pokeapi.co/api/v2/pokemon/25/" to "25",
            "https://pokeapi.co/api/v2/pokemon/1/" to "1",
            "https://pokeapi.co/api/v2/pokemon/151/" to "151",
            "https://pokeapi.co/api/v2/pokemon/pikachu/" to "pikachu"
        )

        testUrls.forEach { (url, expectedId) ->
            // When
            val extractedId = url.split("/").filter { it.isNotEmpty() }.lastOrNull()
            
            // Then
            assertThat(extractedId).isEqualTo(expectedId)
        }
    }

    @Test
    fun `Pokemon name formatting works correctly`() = runTest(testDispatcher) {
        // Given
        val testNames = mapOf(
            "pikachu" to "Pikachu",
            "charmander" to "Charmander",
            "mr-mime" to "Mr-mime",
            "ho-oh" to "Ho-oh"
        )

        testNames.forEach { (original, expected) ->
            // When
            val formatted = original.replaceFirstChar { it.titlecase() }
            
            // Then
            assertThat(formatted).isEqualTo(expected)
        }
    }

    @Test
    fun `Pokemon type list filtering works correctly`() = runTest(testDispatcher) {
        // Given
        val pokemonTypes = listOf("Fire", "Water", "Electric", "Grass", "Poison")
        val searchTerms = mapOf(
            "fir" to listOf("Fire"),
            "wat" to listOf("Water"),
            "e" to listOf("Fire", "Water", "Electric"),
            "poison" to listOf("Poison"),
            "xyz" to emptyList<String>()
        )

        searchTerms.forEach { (searchTerm, expectedResults) ->
            // When
            val filtered = pokemonTypes.filter { 
                it.contains(searchTerm, ignoreCase = true) 
            }
            
            // Then
            assertThat(filtered).containsExactlyElementsIn(expectedResults)
        }
    }

    @Test
    fun `PokemonDisplayItem creation and modification`() = runTest(testDispatcher) {
        // Given
        val originalPokemon = PokemonDisplayItem(
            name = "pikachu",
            detailUrl = "https://pokeapi.co/api/v2/pokemon/25/",
            imageUrl = "https://example.com/pikachu.png",
            isFavorite = false
        )

        // When - marcar como favorito
        val favoritePokemon = originalPokemon.copy(isFavorite = true)
        
        // Then
        assertThat(originalPokemon.isFavorite).isFalse()
        assertThat(favoritePokemon.isFavorite).isTrue()
        assertThat(favoritePokemon.name).isEqualTo(originalPokemon.name)
        assertThat(favoritePokemon.detailUrl).isEqualTo(originalPokemon.detailUrl)
    }

    @Test
    fun `Pokemon search functionality`() = runTest(testDispatcher) {
        // Given
        val pokemonList = listOf(
            PokemonDisplayItem("pikachu", "url1", "img1"),
            PokemonDisplayItem("charmander", "url2", "img2"),
            PokemonDisplayItem("charmeleon", "url3", "img3"),
            PokemonDisplayItem("charizard", "url4", "img4"),
            PokemonDisplayItem("squirtle", "url5", "img5")
        )

        val searchTests = mapOf(
            "pika" to 1,
            "char" to 3,
            "squir" to 1,
            "xyz" to 0,
            "" to 5
        )

        searchTests.forEach { (searchTerm, expectedCount) ->
            // When
            val filtered = if (searchTerm.isBlank()) {
                pokemonList
            } else {
                pokemonList.filter { 
                    it.name.contains(searchTerm, ignoreCase = true) 
                }
            }
            
            // Then
            assertThat(filtered).hasSize(expectedCount)
        }
    }

    @Test
    fun `Pokemon stats processing`() = runTest(testDispatcher) {
        // Given
        val statHolders = listOf(
            PokemonStatHolder(55, 0, PokemonStatInfo("hp", "url")),
            PokemonStatHolder(40, 0, PokemonStatInfo("attack", "url")),
            PokemonStatHolder(50, 0, PokemonStatInfo("defense", "url")),
            PokemonStatHolder(90, 0, PokemonStatInfo("speed", "url"))
        )

        // When
        val statsMap = statHolders.associate { 
            it.stat.name.replaceFirstChar { char -> char.titlecase() } to it.baseStat 
        }
        val totalStats = statHolders.sumOf { it.baseStat }
        val maxStat = statHolders.maxByOrNull { it.baseStat }

        // Then
        assertThat(statsMap).hasSize(4)
        assertThat(statsMap["Hp"]).isEqualTo(55)
        assertThat(statsMap["Attack"]).isEqualTo(40)
        assertThat(statsMap["Speed"]).isEqualTo(90)
        assertThat(totalStats).isEqualTo(235)
        assertThat(maxStat?.stat?.name).isEqualTo("speed")
        assertThat(maxStat?.baseStat).isEqualTo(90)
    }

    @Test
    fun `List chunking for parallel processing`() = runTest(testDispatcher) {
        // Given
        val pokemonList = (1..50).map { 
            PokemonListItem("pokemon$it", "url$it") 
        }

        // When
        val chunkedBy10 = pokemonList.chunked(10)
        val chunkedBy15 = pokemonList.chunked(15)

        // Then
        assertThat(chunkedBy10).hasSize(5) // 50/10 = 5 chunks
        assertThat(chunkedBy10.first()).hasSize(10)
        assertThat(chunkedBy10.last()).hasSize(10)
        
        assertThat(chunkedBy15).hasSize(4) // 50/15 = 3.33, so 4 chunks
        assertThat(chunkedBy15.first()).hasSize(15)
        assertThat(chunkedBy15.last()).hasSize(5) // remainder
    }

    @Test
    fun `Pokemon type checking and validation`() = runTest(testDispatcher) {
        // Given
        val availableTypes = listOf(
            "Normal", "Fire", "Water", "Electric", "Grass", "Ice", 
            "Fighting", "Poison", "Ground", "Flying", "Psychic", 
            "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy"
        )
        
        val testTypes = listOf("fire", "WATER", "Electric", "grass", "invalid")

        testTypes.forEach { testType ->
            // When
            val isValidType = availableTypes.any { 
                it.equals(testType, ignoreCase = true) 
            }
            
            // Then
            when (testType.lowercase()) {
                "fire", "water", "electric", "grass" -> assertThat(isValidType).isTrue()
                "invalid" -> assertThat(isValidType).isFalse()
            }
        }
    }
} 