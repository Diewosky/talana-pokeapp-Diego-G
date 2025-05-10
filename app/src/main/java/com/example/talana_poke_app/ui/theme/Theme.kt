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

private val DarkColorScheme = darkColorScheme(
    primary = PokemonRed, // Example: Using PokemonRed for primary
    secondary = PokemonYellow, // Example: Using PokemonYellow for secondary
    tertiary = PokemonBlue,
    background = PokemonDarkGray, // Example: Dark gray for background
    surface = PokemonDarkGray, // Example: Dark gray for surfaces
    onPrimary = Color.White,
    onSecondary = PokemonDarkBlue,
    onTertiary = Color.White,
    onBackground = PokemonLightGray, // Light text on dark background
    onSurface = PokemonLightGray // Light text on dark surfaces
)

private val LightColorScheme = lightColorScheme(
    primary = PokemonRed,
    secondary = PokemonBlue,
    tertiary = PokemonYellow,
    background = PokemonLightGray, // Example: Light gray for background
    surface = Color.White,      // White for surfaces like cards
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = PokemonDarkBlue,
    onBackground = PokemonDarkBlue, // Dark text on light background
    onSurface = PokemonDarkBlue    // Dark text on light surfaces

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun TalanaPokeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 