package com.example.talana_poke_app.presentation.pokemonlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.talana_poke_app.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PokemonListScreen(
    navController: NavController,
    pokemonViewModel: PokemonViewModel = viewModel(),
    listType: PokemonListType,
) {
    val uiState by pokemonViewModel.uiState.collectAsState()
    val searchQuery by pokemonViewModel.searchQuery.collectAsState()

    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedPokemonForDialog by remember { mutableStateOf<PokemonDisplayItem?>(null) }
    
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(listType) {
        pokemonViewModel.loadPokemons(listType)
    }

    Scaffold(
        topBar = {
            if (listType == PokemonListType.ALL) {
                // Barra superior roja con búsqueda integrada
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PokemonRed)
                        .padding(bottom = 12.dp)
                ) {
                    // Espacio superior para evitar solapamiento con la hora
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Navegación y título
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón de retroceso con mejor alineación
                        Box(
                            modifier = Modifier
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
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = "Todos los Pokémon",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    
                    // Campo de búsqueda con estilo retro
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Sombra pixelada
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 4.dp, top = 4.dp)
                                .height(56.dp)
                                .clip(RoundedCornerShape(0.dp))
                                .background(Color(0xFF555555))
                        )
                        
                        // Caja principal estilo retro
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(0.dp))
                                .background(Color.White.copy(alpha = 0.9f))
                                .border(width = 4.dp, color = Color.Black)
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
                                }
                        ) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { pokemonViewModel.updateSearchQuery(it) },
                                placeholder = { 
                                    Text(
                                        "Buscar Pokémon...",
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 0.5.sp
                                    ) 
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = PokemonRed,
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = "Buscar",
                                        tint = Color.Gray,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { pokemonViewModel.updateSearchQuery("") }) {
                                            Icon(
                                                imageVector = Icons.Filled.Clear,
                                                contentDescription = "Limpiar búsqueda",
                                                tint = Color.Gray
                                            )
                                        }
                                    }
                                },
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }
            } else {
                // Barra superior para Favoritos (solo roja sin búsqueda)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PokemonRed)
                ) {
                    // Espacio superior para evitar solapamiento con la hora
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Barra con título y navegación
                    TopAppBar(
                        title = {
                            Text(
                                text = "Mis Favoritos",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
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
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = PokemonRed,
                            titleContentColor = Color.White
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(color = PokemonRed)
                }
                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    PokemonLazyList(
                        pokemonList = uiState.pokemonList,
                        onFavoriteToggle = { pokemon -> pokemonViewModel.toggleFavorite(pokemon) },
                        onPokemonClick = { pokemon ->
                            selectedPokemonForDialog = pokemon
                            showDetailDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDetailDialog && selectedPokemonForDialog != null) {
        // Registrar que se ha visto un detalle de Pokémon
        LaunchedEffect(selectedPokemonForDialog) {
            selectedPokemonForDialog?.let {
                pokemonViewModel.onPokemonDetailViewed(it)
            }
        }
        
        PokemonDetailDialog(
            pokemon = selectedPokemonForDialog!!,
            onDismissRequest = { showDetailDialog = false }
        )
    }
}

@Composable
fun PokemonLazyList(
    pokemonList: List<PokemonDisplayItem>,
    modifier: Modifier = Modifier,
    onFavoriteToggle: (PokemonDisplayItem) -> Unit,
    onPokemonClick: (PokemonDisplayItem) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pokemonList, key = { it.name }) { pokemon ->
            PokemonRow(
                pokemon = pokemon,
                onFavoriteToggle = onFavoriteToggle,
                onPokemonClick = onPokemonClick
            )
        }
    }
}

@Composable
fun PokemonRow(
    pokemon: PokemonDisplayItem,
    modifier: Modifier = Modifier,
    onFavoriteToggle: (PokemonDisplayItem) -> Unit,
    onPokemonClick: (PokemonDisplayItem) -> Unit
) {
    // Box contenedor con efecto de sombra pixelada
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        // Sombra
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 4.dp)
                .clip(RoundedCornerShape(0.dp))
                .background(Color(0xFF555555))
                .height(88.dp)
        )
        
        // Tarjeta principal con borde pixelado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(0.dp))
                .background(Color.White)
                .border(width = 4.dp, color = Color.Black)
                .clickable { onPokemonClick(pokemon) }
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
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Fondo para la imagen del Pokémon con estilo pixelado
                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(64.dp)
                        .clip(RoundedCornerShape(0.dp))
                        .border(width = 2.dp, color = Color.Black)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = pokemon.imageUrl,
                        contentDescription = "${pokemon.name} image",
                        modifier = Modifier
                            .width(56.dp)
                            .height(56.dp),
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = pokemon.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                    
                    // Tipos de Pokémon con estilo pixelado
                    pokemon.types?.let { types ->
                        if (types.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                types.take(2).forEach { type ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(0.dp))
                                            .border(width = 1.dp, color = Color.Black)
                                            .background(
                                                color = getTypeColor(type).copy(alpha = 0.8f)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = type,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Estrella de favorito con estilo pixelado
                Icon(
                    imageVector = if (pokemon.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (pokemon.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (pokemon.isFavorite) Color(0xFFFFD700) else Color.Gray,
                    modifier = Modifier
                        .width(32.dp)
                        .height(32.dp)
                        .clickable { onFavoriteToggle(pokemon) }
                        .padding(4.dp)
                )
            }
        }
    }
}

// Función para obtener color según el tipo de Pokémon
@Composable
fun getTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "normal" -> TypeNormal
        "fire" -> TypeFire
        "water" -> TypeWater
        "electric" -> TypeElectric
        "grass" -> TypeGrass
        "ice" -> TypeIce
        "fighting" -> TypeFighting
        "poison" -> TypePoison
        "ground" -> TypeGround
        "flying" -> TypeFlying
        "psychic" -> TypePsychic
        "bug" -> TypeBug
        "rock" -> TypeRock
        "ghost" -> TypeGhost
        "dragon" -> TypeDragon
        "dark" -> TypeDark
        "steel" -> TypeSteel
        "fairy" -> TypeFairy
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
}

@Composable
fun Chip(
    label: String, 
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondaryContainer
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color,
        contentColor = Color.White,
        tonalElevation = 1.dp
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun PokemonDetailDialog(
    pokemon: PokemonDisplayItem,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        // Sombra para efecto pixelado
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f)
        ) {
            // Sombra
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 5.dp, top = 5.dp)
                    .clip(RoundedCornerShape(0.dp))
                    .background(Color(0xFF555555))
            )
            
            // Contenido principal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(0.dp))
                    .background(Color.White)
                    .border(width = 4.dp, color = Color.Black)
                    .drawBehind {
                        // Líneas horizontales para efecto pixelado
                        for (y in 0..size.height.toInt() step 4) {
                            drawLine(
                                color = Color.LightGray.copy(alpha = 0.2f),
                                start = Offset(0f, y.toFloat()),
                                end = Offset(size.width, y.toFloat()),
                                strokeWidth = 1f
                            )
                        }
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Encabezado con imagen y nombre (FIJO)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Fondo para la imagen
                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .drawBehind {
                                        // Círculo de fondo con gradiente sutil
                                        drawCircle(
                                            color = Color.LightGray.copy(alpha = 0.2f),
                                            radius = size.width / 2
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = pokemon.imageUrl,
                                    contentDescription = "${pokemon.name} image",
                                    modifier = Modifier.size(120.dp)
                                )
                            }

                            Text(
                                text = pokemon.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = Color.DarkGray
                                )
                            )
                            
                            // Fila con tipos de Pokémon
                            pokemon.types?.let { types ->
                                if (types.isNotEmpty()) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        types.forEach { typeName -> 
                                            TypeChip(typeName = typeName)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Línea divisoria estilo retro (línea punteada)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .drawBehind {
                                for (x in 0..size.width.toInt() step 8) {
                                    drawLine(
                                        color = Color.Gray.copy(alpha = 0.5f),
                                        start = Offset(x.toFloat(), 0f),
                                        end = Offset(x.toFloat() + 4, 0f),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }
                            }
                    )

                    // CONTENIDO DESPLAZABLE (solo esta parte es scrollable)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Información básica
                            PixelatedCard(
                                title = "Información básica",
                                color = Color(0xFFFFF3E0).copy(alpha = 0.8f),
                                content = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        pokemon.height?.let { 
                                            InfoItem(
                                                title = "Altura",
                                                value = "${it / 10.0} m",
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        pokemon.weight?.let { 
                                            InfoItem(
                                                title = "Peso",
                                                value = "${it / 10.0} kg",
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                    
                                    // Estado de favorito
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (pokemon.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                            contentDescription = "Favorite status",
                                            tint = if (pokemon.isFavorite) Color(0xFFFFD700) else Color.Gray,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (pokemon.isFavorite) "Favorito" else "No es favorito",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            )

                            // Habilidades
                            pokemon.abilities?.let { abilities ->
                                if (abilities.isNotEmpty()) {
                                    PixelatedCard(
                                        title = "Habilidades",
                                        color = Color(0xFFE0F7FA).copy(alpha = 0.8f),
                                        content = {
                                            Column(
                                                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                abilities.forEach { abilityName ->
                                                    Text(
                                                        text = "• $abilityName",
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            letterSpacing = 0.5.sp
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                            }

                            // Estadísticas
                            pokemon.stats?.let { stats ->
                                if (stats.isNotEmpty()) {
                                    PixelatedCard(
                                        title = "Estadísticas Base",
                                        color = Color(0xFFF5F5F5).copy(alpha = 0.8f),
                                        content = {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                stats.forEach { (statName, statValue) ->
                                                    PixelatedStatBar(
                                                        statName = statName,
                                                        statValue = statValue
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                            
                            // Espacio adicional al final para asegurar que todo sea visible al hacer scroll
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Botón cerrar (FIJO)
                    Spacer(modifier = Modifier.height(16.dp))
                    PixelatedButton(
                        onClick = onDismissRequest,
                        text = "CERRAR",
                        color = PokemonRed,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun TypeChip(typeName: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(0.dp))
            .border(width = 2.dp, color = Color.Black)
            .background(color = getTypeColor(typeName))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = typeName,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun PixelatedCard(
    title: String,
    color: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Sombra
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 3.dp, top = 3.dp)
                .clip(RoundedCornerShape(0.dp))
                .background(Color(0xFF555555))
                .padding(vertical = 12.dp)
        )
        
        // Contenido
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(0.dp))
                .background(color)
                .border(width = 2.dp, color = Color.Black)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = title, 
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = Color.DarkGray
                )
            )
            
            content()
        }
    }
}

@Composable
fun PixelatedStatBar(statName: String, statValue: Int, maxValue: Int = 255) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = statName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = statValue.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Barra de progreso pixelada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(0.dp))
                .border(width = 1.dp, color = Color.Black)
                .background(Color.LightGray.copy(alpha = 0.3f))
        ) {
            // Calculamos el porcentaje del valor respecto al máximo
            val percentage = (statValue.toFloat() / maxValue).coerceIn(0f, 1f)
            
            // Color de la barra según el valor
            val barColor = when {
                statValue < 50 -> Color(0xFFEF5350) // Rojo para valores bajos
                statValue < 100 -> Color(0xFFFFB74D) // Naranja para valores medios
                else -> Color(0xFF66BB6A) // Verde para valores altos
            }
            
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage)
                    .clip(RoundedCornerShape(0.dp))
                    .background(barColor)
                    .drawBehind {
                        // Líneas verticales para efecto pixelado en la barra
                        for (x in 0..size.width.toInt() step 4) {
                            drawLine(
                                color = barColor.copy(alpha = 0.7f),
                                start = Offset(x.toFloat(), 0f),
                                end = Offset(x.toFloat(), size.height),
                                strokeWidth = 2f
                            )
                        }
                    }
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

@Composable
fun InfoItem(title: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun PokemonListScreenPreview() {
    TalanaPokeAppTheme {
        // Necesitarías un AuthViewModel mock/fake aquí o un PokemonViewModel mock
        // PokemonListScreen(onSignOut = {})
    }
}
*/ 