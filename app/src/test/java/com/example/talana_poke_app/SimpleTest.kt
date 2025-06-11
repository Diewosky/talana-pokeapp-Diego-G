package com.example.talana_poke_app

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Test simple para verificar que el framework de testing funciona correctamente
 */
class SimpleTest {

    @Test
    fun `basic truth assertion works`() {
        // Given
        val testString = "TalanaPokeApp"
        val testNumber = 42

        // When/Then
        assertThat(testString).isEqualTo("TalanaPokeApp")
        assertThat(testString).contains("Poke")
        assertThat(testNumber).isEqualTo(42)
        assertThat(testNumber).isGreaterThan(0)
    }

    @Test
    fun `basic list operations work`() {
        // Given
        val pokemonTypes = listOf("Fire", "Water", "Electric", "Grass")

        // When/Then
        assertThat(pokemonTypes).hasSize(4)
        assertThat(pokemonTypes).contains("Fire")
        assertThat(pokemonTypes).containsAtLeast("Water", "Electric")
        assertThat(pokemonTypes.first()).isEqualTo("Fire")
    }

    @Test
    fun `testing utilities work correctly`() {
        // Given
        val testData = TestUtils.createTestPokemonListItem("pikachu", 25)

        // When/Then
        assertThat(testData.name).isEqualTo("pikachu")
        assertThat(testData.url).contains("pokemon/25")
    }

    @Test
    fun `data class equality works`() {
        // Given
        val pokemon1 = TestUtils.createTestPokemonDisplayItem("pikachu", 25)
        val pokemon2 = TestUtils.createTestPokemonDisplayItem("pikachu", 25)
        val pokemon3 = TestUtils.createTestPokemonDisplayItem("charmander", 4)

        // When/Then
        assertThat(pokemon1).isEqualTo(pokemon2)
        assertThat(pokemon1).isNotEqualTo(pokemon3)
        assertThat(pokemon1.name).isEqualTo(pokemon2.name)
    }
} 