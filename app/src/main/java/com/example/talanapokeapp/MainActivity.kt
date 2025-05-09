package com.example.talanapokeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.talanapokeapp.ui.theme.TalanaPokeAppTheme
import com.example.talana_poke_app.presentation.pokemonlist.PokemonListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TalanaPokeAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PokemonListScreen(modifier = Modifier.fillMaxSize() /* Modifier.padding(innerPadding) can be applied here if needed */)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonListScreenPreview() {
    TalanaPokeAppTheme {
        PokemonListScreen()
    }
}