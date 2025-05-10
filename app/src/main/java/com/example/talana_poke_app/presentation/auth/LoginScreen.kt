package com.example.talana_poke_app.presentation.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.talana_poke_app.ui.theme.PokemonRed
import com.example.talana_poke_app.ui.theme.PokemonYellow

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit // Callback para navegar después del login exitoso
) {
    val uiState by authViewModel.uiState.collectAsState()

    // ActivityResultLauncher para el intent de Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val data = result.data
            authViewModel.handleGoogleSignInResult(data)
        }
    )

    // Observar el trigger para lanzar el intent de Google Sign-In
    LaunchedEffect(key1 = uiState.googleSignInTriggered) {
        if (uiState.googleSignInTriggered) {
            googleSignInLauncher.launch(authViewModel.googleSignInClient.signInIntent)
            authViewModel.consumeSignInTrigger() // Consumir el trigger después de lanzar
        }
    }

    // Navegar si el login es exitoso y el usuario está disponible
    LaunchedEffect(key1 = uiState.currentUser) {
        if (uiState.currentUser != null) {
            onLoginSuccess()
        }
    }

    // Contenido principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo o título
        Text(
            "Talana PokéApp", 
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "Explora el mundo Pokémon", 
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(64.dp))

        if (uiState.isLoading && !uiState.googleSignInTriggered) {
            CircularProgressIndicator(
                color = PokemonYellow
            )
        } else {
            // Botón normal de login
            Button(
                onClick = { authViewModel.triggerGoogleSignIn() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PokemonRed
                )
            ) {
                Text(
                    "Iniciar sesión con Google",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error, 
                color = MaterialTheme.colorScheme.error
            )
        }
    }
} 