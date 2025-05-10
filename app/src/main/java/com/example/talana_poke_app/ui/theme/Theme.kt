package com.example.talana_poke_app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de colores oscuro mejorado
private val DarkColorScheme = darkColorScheme(
    primary = PokemonRed,
    primaryContainer = PokemonRedDark,
    onPrimaryContainer = Color.White,
    secondary = PokemonBlue,
    secondaryContainer = PokemonBlueDark,
    onSecondaryContainer = Color.White,
    tertiary = PokemonYellow,
    tertiaryContainer = PokemonYellowDark,
    onTertiaryContainer = PokemonDarkBlue,
    background = PokemonDarkGray,
    surface = PokemonSurfaceDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = PokemonDarkBlue,
    onBackground = PokemonLightGray,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF424242), // Superficies secundarias (cards, botones, etc.)
    onSurfaceVariant = Color(0xFFB0B0B0), // Texto e iconos secundarios
    surfaceTint = PokemonRed, // Tinte para elevaciones
    inverseSurface = Color(0xFFEEEEEE), // Superficie invertida para énfasis
    inverseOnSurface = Color(0xFF303030), // Texto sobre superficie invertida
    error = Color(0xFFCF6679), // Error
    onError = Color.White, // Texto sobre error
    errorContainer = Color(0xFFB00020), // Contenedor de error
    onErrorContainer = Color.White, // Texto sobre contenedor de error
    surfaceContainerHigh = Color(0xFF404040) // Superficie de diálogos
)

// Esquema de colores claro mejorado
private val LightColorScheme = lightColorScheme(
    primary = PokemonRed,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = PokemonRedDark,
    secondary = PokemonBlue,
    secondaryContainer = Color(0xFFD6E6FF),
    onSecondaryContainer = PokemonBlueDark,
    tertiary = PokemonYellow,
    tertiaryContainer = Color(0xFFFFEECC),
    onTertiaryContainer = PokemonYellowDark,
    background = PokemonLightGray,
    surface = PokemonSurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = PokemonDarkBlue,
    onBackground = PokemonDarkBlue,
    onSurface = PokemonDarkBlue,
    surfaceVariant = Color(0xFFEEEEEE), // Superficies secundarias (cards, botones, etc.)
    onSurfaceVariant = Color(0xFF666666), // Texto e iconos secundarios
    surfaceTint = PokemonRed, // Tinte para elevaciones
    inverseSurface = Color(0xFF303030), // Superficie invertida para énfasis
    inverseOnSurface = Color(0xFFEEEEEE), // Texto sobre superficie invertida
    error = Color(0xFFB00020), // Error
    onError = Color.White, // Texto sobre error
    errorContainer = Color(0xFFFDE7E9), // Contenedor de error
    onErrorContainer = Color(0xFFB00020), // Texto sobre contenedor de error
    surfaceContainerHigh = Color(0xFFF5F5F5) // Superficie de diálogos
)

@Composable
fun TalanaPokeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color está disponible en Android 12+
    dynamicColor: Boolean = false, // Desactivado por defecto para mantener la identidad de marca
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Configura el color de la barra de estado
            window.statusBarColor = colorScheme.primary.toArgb()
            // Configura el estilo de los iconos de la barra de estado
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 