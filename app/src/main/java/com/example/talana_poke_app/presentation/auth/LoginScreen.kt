package com.example.talana_poke_app.presentation.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
// import androidx.compose.foundation.layout.size // No se usa directamente aquí
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// import androidx.compose.ui.platform.LocalContext // No se usa directamente aquí
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel 
// import com.google.android.gms.common.SignInButton // Comentado por ahora

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit // Callback para navegar después del login exitoso
) {
    val uiState by authViewModel.uiState.collectAsState()
    // val context = LocalContext.current // No se necesita si el signInIntent se obtiene del ViewModel

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido a TalanaPokeApp", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoading && !uiState.googleSignInTriggered) { // Mostrar CircularProgress si está cargando PERO no esperando el intent
            CircularProgressIndicator()
        } else {
            Button(onClick = { authViewModel.triggerGoogleSignIn() }) {
                Text("Iniciar sesión con Google")
            }
        }

        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }
    }
} 