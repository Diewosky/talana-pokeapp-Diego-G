package com.example.talana_poke_app

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test de instrumentación simple para verificar que el framework funciona
 */
@RunWith(AndroidJUnit4::class)
class SimpleInstrumentedTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = ApplicationProvider.getApplicationContext<PokemonApplication>()
        assertThat(appContext.packageName).isEqualTo("com.example.talanapokeapp")
    }

    @Test
    fun basicInstrumentedAssertions() {
        // Given
        val testString = "Instrumented Test"
        val testList = listOf("Pikachu", "Charmander", "Bulbasaur")

        // When/Then
        assertThat(testString).contains("Test")
        assertThat(testList).hasSize(3)
        assertThat(testList).contains("Pikachu")
    }
} 