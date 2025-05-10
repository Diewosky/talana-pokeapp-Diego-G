package com.example.talana_poke_app.presentation.mainmenu

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.talana_poke_app.presentation.auth.AuthViewModel
import com.example.talana_poke_app.presentation.navigation.Screen
import com.example.talana_poke_app.presentation.pokemonlist.PokemonListType
import com.example.talana_poke_app.ui.theme.PokemonBlue
import com.example.talana_poke_app.ui.theme.PokemonRed
import com.example.talana_poke_app.ui.theme.PokemonYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.uiState.collectAsState()
    val userName = authState.currentUser?.displayName ?: authState.currentUser?.email ?: "Entrenador"

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PokemonRed)
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¡Bienvenido!",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Nombre de usuario con estilo pixelado
                Box(
                    modifier = Modifier
                        .padding(horizontal =
                        12.dp, vertical = 4.dp)
                        .border(
                            width = 4.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(0.dp)
                        )
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(0.dp),
                            spotColor = Color.Black
                        )
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val annotatedString = buildAnnotatedString {
                        for (i in userName.indices) {
                            val char = userName[i].toString()
                            val color = when {
                                i % 3 == 0 -> PokemonRed
                                i % 3 == 1 -> PokemonBlue
                                else -> PokemonYellow
                            }
                            
                            withStyle(
                                style = SpanStyle(
                                    color = color,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp,
                                    letterSpacing = 2.sp,
                                    background = Color.White
                                )
                            ) {
                                append(char)
                            }
                        }
                    }
                    
                    Text(
                        text = annotatedString,
                        modifier = Modifier
                            .drawBehind {
                                // Líneas horizontales para efecto pixelado
                                for (y in 0..size.height.toInt() step 4) {
                                    drawLine(
                                        color = Color.Black.copy(alpha = 0.05f),
                                        start = Offset(0f, y.toFloat()),
                                        end = Offset(size.width, y.toFloat()),
                                        strokeWidth = 1f
                                    )
                                }
                            }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Botón Todos los Pokémon
            PixelatedButton(
                onClick = {
                    navController.navigate(Screen.PokemonList.route + "/${PokemonListType.ALL.name}")
                },
                text = "TODOS LOS POKÉMON",
                color = PokemonRed,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Mis Favoritos
            PixelatedButton(
                onClick = {
                    navController.navigate(Screen.PokemonList.route + "/${PokemonListType.FAVORITES.name}")
                },
                text = "MIS FAVORITOS",
                color = PokemonRed,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Botón Estadísticas
            PixelatedButton(
                onClick = {
                    navController.navigate(Screen.Stats.route)
                },
                text = "ESTADÍSTICAS",
                color = PokemonBlue,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Botón de cerrar sesión 
            PixelatedButton(
                onClick = {
                    authViewModel.signOut()
                },
                text = "CERRAR SESIÓN",
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PixelatedButton(
    onClick: () -> Unit,
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    // Colores
    val mainColor = color
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