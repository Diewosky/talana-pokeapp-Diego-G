package com.example.talana_poke_app.presentation.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.talanapokeapp.R
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
        // Logo de Pokémon
        Image(
            painter = painterResource(id = R.drawable.pokemon_logo),
            contentDescription = "Pokemon Logo",
            modifier = Modifier
                .size(250.dp)
                .padding(bottom = 16.dp)
        )
        
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
            // Botón estilo pixelado Pokémon
            PixelatedButton(
                onClick = { authViewModel.triggerGoogleSignIn() },
                text = "INICIAR AVENTURA",
                modifier = Modifier.fillMaxWidth()
            )
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

@Composable
fun PixelatedButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    // Colores al estilo Game Boy
    val mainColor = PokemonRed
    val borderColor = Color.Black
    val shadowColor = Color(0xFF555555)
    val textColor = Color.White
    
    // Estructura de capas para simular efecto pixelado
    Box(
        modifier = modifier
            .height(64.dp)
    ) {
        // Capa de sombra pixelada (4dp offset)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 4.dp, y = 4.dp)
                .clip(RoundedCornerShape(0.dp))  // Esquinas cuadradas, estilo pixelado
                .background(shadowColor)
        )
        
        // Capa base del botón con efecto pixelado
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 0.dp, y = 0.dp)
                .clip(RoundedCornerShape(0.dp))  // Esquinas cuadradas, estilo pixelado
                .background(mainColor)
                .border(width = 4.dp, color = borderColor)
                .drawBehind {
                    // Líneas horizontales para efecto pixelado
                    for (y in 0..size.height.toInt() step 4) {
                        drawLine(
                            color = mainColor.copy(alpha = 0.7f),
                            start = Offset(0f, y.toFloat()),
                            end = Offset(size.width, y.toFloat()),
                            strokeWidth = 2f
                        )
                    }
                    
                    // Borde interior para efecto 3D pixelado
                    drawLine(
                        color = Color.White.copy(alpha = 0.3f),
                        start = Offset(4f, 4f),
                        end = Offset(size.width - 4f, 4f),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.3f),
                        start = Offset(4f, 4f),
                        end = Offset(4f, size.height - 4f),
                        strokeWidth = 2f
                    )
                    
                    // Borde oscuro para efecto 3D pixelado
                    drawLine(
                        color = Color.Black.copy(alpha = 0.3f),
                        start = Offset(4f, size.height - 4f),
                        end = Offset(size.width - 4f, size.height - 4f),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color.Black.copy(alpha = 0.3f),
                        start = Offset(size.width - 4f, 4f),
                        end = Offset(size.width - 4f, size.height - 4f),
                        strokeWidth = 2f
                    )
                }
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            // Texto con estilo pixelado
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
} 