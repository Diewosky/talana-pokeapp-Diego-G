package com.example.talana_poke_app.presentation.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.talana_poke_app.data.model.UsageStats
import com.example.talana_poke_app.ui.theme.PokemonRed
import com.example.talana_poke_app.ui.theme.PokemonBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    navController: NavController,
    usageStatsViewModel: UsageStatsViewModel = viewModel()
) {
    val usageStats by usageStatsViewModel.usageStats.collectAsState()
    
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PokemonRed)
            ) {
                // Espacio superior para evitar solapamiento con la hora
                Spacer(modifier = Modifier.height(8.dp))
                
                TopAppBar(
                    title = { 
                        Text(
                            "Estadísticas de Uso", 
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 22.sp,
                            letterSpacing = 0.5.sp
                        ) 
                    },
                    navigationIcon = {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(40.dp)
                                .clip(RoundedCornerShape(0.dp))
                                .border(width = 2.dp, color = Color.White)
                                .clickable { navController.navigateUp() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { usageStatsViewModel.resetStats() }) {
                            Icon(
                                Icons.Filled.Refresh, 
                                contentDescription = "Reiniciar estadísticas",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PokemonRed,
                        titleContentColor = Color.White
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PixelatedStatsCard(
                title = "Pokémon Vistos",
                value = "${usageStats.pokemonViewed}",
                description = "Pokémon visualizados en detalle",
                color = PokemonRed
            )
            
            PixelatedStatsCard(
                title = "Pokémon Favoritos",
                value = "${usageStats.pokemonFavorited}",
                description = "Pokémon marcados como favoritos",
                color = PokemonBlue
            )
            
            PixelatedStatsCard(
                title = "Tiempo de Juego",
                value = usageStats.getFormattedTotalTime(),
                description = "Tiempo total en la aventura Pokémon",
                color = Color(0xFF66BB6A) // Verde para el tiempo
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PixelatedButton(
                onClick = { navController.navigateUp() },
                text = "VOLVER",
                color = PokemonRed,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PixelatedStatsCard(
    title: String,
    value: String,
    description: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Sombra
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 4.dp)
                .clip(RoundedCornerShape(0.dp))
                .background(Color(0xFF555555))
                .height(128.dp)
        )
        
        // Tarjeta principal con borde pixelado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(0.dp))
                .background(Color.White)
                .border(width = 3.dp, color = Color.Black)
                .drawBehind {
                    // Líneas horizontales para efecto pixelado
                    for (y in 0..size.height.toInt() step 4) {
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            start = Offset(0f, y.toFloat()),
                            end = Offset(size.width, y.toFloat()),
                            strokeWidth = 1f
                        )
                    }
                    
                    // Borde interior para efecto 3D pixelado
                    drawLine(
                        color = Color.White.copy(alpha = 0.5f),
                        start = Offset(4f, 4f),
                        end = Offset(size.width - 4f, 4f),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.5f),
                        start = Offset(4f, 4f),
                        end = Offset(4f, size.height - 4f),
                        strokeWidth = 2f
                    )
                }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = color
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
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
    // Box contenedor con efecto de sombra pixelada
    Box(
        modifier = modifier
            .height(48.dp)
    ) {
        // Sombra
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp, top = 4.dp)
                .clip(RoundedCornerShape(0.dp))
                .background(Color(0xFF555555))
        )
        
        // Botón principal con borde pixelado
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(0.dp))
                .background(color)
                .border(width = 3.dp, color = Color.Black)
                .clickable(onClick = onClick)
                .drawBehind {
                    // Líneas horizontales para efecto pixelado
                    for (y in 0..size.height.toInt() step 4) {
                        drawLine(
                            color = color.copy(alpha = 0.7f),
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
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return format.format(date)
} 