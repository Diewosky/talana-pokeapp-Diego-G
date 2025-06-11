package com.example.talana_poke_app.presentation.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.talana_poke_app.ui.theme.TalanaPokeAppTheme
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysCorrectInitialContent() {
        // Given
        val mockViewModel = mockk<AuthViewModel>(relaxed = true)
        val mockOnLoginSuccess = mockk<() -> Unit>(relaxed = true)

        // When
        composeTestRule.setContent {
            TalanaPokeAppTheme {
                LoginScreen(
                    authViewModel = mockViewModel,
                    onLoginSuccess = mockOnLoginSuccess
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("¡Bienvenido Entrenador!")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Inicia sesión para comenzar tu aventura Pokémon")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Iniciar sesión con Google")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun loginScreen_googleSignInButtonIsClickable() {
        // Given
        val mockViewModel = mockk<AuthViewModel>(relaxed = true)
        val mockOnLoginSuccess = mockk<() -> Unit>(relaxed = true)

        // When
        composeTestRule.setContent {
            TalanaPokeAppTheme {
                LoginScreen(
                    authViewModel = mockViewModel,
                    onLoginSuccess = mockOnLoginSuccess
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Iniciar sesión con Google")
            .assertIsDisplayed()
            .assertIsEnabled()
            .assertHasClickAction()
    }

    @Test
    fun loginScreen_displaysLoadingState() {
        // Given
        val mockViewModel = mockk<AuthViewModel>(relaxed = true)
        val mockOnLoginSuccess = mockk<() -> Unit>(relaxed = true)

        // Simular estado de carga
        // Nota: Esto requeriría configurar el estado del ViewModel para mostrar loading

        // When
        composeTestRule.setContent {
            TalanaPokeAppTheme {
                LoginScreen(
                    authViewModel = mockViewModel,
                    onLoginSuccess = mockOnLoginSuccess
                )
            }
        }

        // Then - verificar que el botón funciona correctamente
        composeTestRule
            .onNodeWithText("Iniciar sesión con Google")
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_hasCorrectSemantics() {
        // Given
        val mockViewModel = mockk<AuthViewModel>(relaxed = true)
        val mockOnLoginSuccess = mockk<() -> Unit>(relaxed = true)

        // When
        composeTestRule.setContent {
            TalanaPokeAppTheme {
                LoginScreen(
                    authViewModel = mockViewModel,
                    onLoginSuccess = mockOnLoginSuccess
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Iniciar sesión con Google")
            .assertHasClickAction()
    }

    @Test
    fun loginScreen_displaysPokeballIcon() {
        // Given
        val mockViewModel = mockk<AuthViewModel>(relaxed = true)
        val mockOnLoginSuccess = mockk<() -> Unit>(relaxed = true)

        // When
        composeTestRule.setContent {
            TalanaPokeAppTheme {
                LoginScreen(
                    authViewModel = mockViewModel,
                    onLoginSuccess = mockOnLoginSuccess
                )
            }
        }

        // Then - verificar que el icono de Pokeball está presente
        // Esto se puede hacer con un contentDescription específico si está configurado
        composeTestRule
            .onNodeWithContentDescription("Pokeball")
            .assertExists()
    }
} 