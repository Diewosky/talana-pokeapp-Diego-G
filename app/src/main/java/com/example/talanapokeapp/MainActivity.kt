package com.example.talanapokeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.talana_poke_app.presentation.auth.AuthViewModel
import com.example.talana_poke_app.presentation.auth.LoginScreen
import com.example.talana_poke_app.presentation.pokemonlist.PokemonListScreen
import com.example.talanapokeapp.ui.theme.TalanaPokeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TalanaPokeAppTheme {
                val authViewModel: AuthViewModel = viewModel()
                val authUiState by authViewModel.uiState.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val screenModifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()

                    if (authUiState.currentUser == null) {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onLoginSuccess = { }
                        )
                    } else {
                        PokemonListScreen(
                            modifier = screenModifier,
                            authViewModel = authViewModel,
                            onSignOut = { }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultAppPreview() { 
    TalanaPokeAppTheme {
        LoginScreen(onLoginSuccess = {})
    }
}