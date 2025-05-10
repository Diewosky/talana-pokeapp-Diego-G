package com.example.talana_poke_app.presentation.pokemonlist

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
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
    
    // Estado para mostrar/ocultar la barra de búsqueda
    var showSearchBar by remember { mutableStateOf(false) }
    
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(listType) {
        pokemonViewModel.loadPokemons(listType)
    }

    Scaffold(
        topBar = {
            if (showSearchBar && listType == PokemonListType.ALL) {
                // Barra de búsqueda
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { pokemonViewModel.updateSearchQuery(it) },
                    onSearch = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },
                    onClose = { 
                        showSearchBar = false
                        pokemonViewModel.updateSearchQuery("")
                    },
                    focusRequester = focusRequester
                )
            } else {
                // Barra superior normal
                TopAppBar(
                    title = {
                        Text(if (listType == PokemonListType.ALL) "Todos los Pokémon" else "Mis Favoritos")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                        if (listType == PokemonListType.ALL) {
                            IconButton(onClick = { showSearchBar = true }) {
                                Icon(Icons.Filled.Search, contentDescription = "Buscar")
                            }
                        }
                    }
                )
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
                    CircularProgressIndicator()
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

    // Efecto para mostrar el teclado cuando se abre la barra de búsqueda
    LaunchedEffect(showSearchBar) {
        if (showSearchBar) {
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
                // Manejar excepción si ocurre
            }
        }
    }

    if (showDetailDialog && selectedPokemonForDialog != null) {
        PokemonDetailDialog(
            pokemon = selectedPokemonForDialog!!,
            onDismissRequest = { showDetailDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClose: () -> Unit,
    focusRequester: FocusRequester
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Buscar Pokémon...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Cerrar búsqueda")
            }
        }
    )
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPokemonClick(pokemon) }
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Fondo circular para la imagen del Pokémon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = "${pokemon.name} image",
                    modifier = Modifier.size(60.dp),
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = pokemon.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                // Añadir tipos si están disponibles
                pokemon.types?.let { types ->
                    if (types.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            types.take(2).forEach { type ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = getTypeColor(type).copy(alpha = 0.8f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = type,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            IconButton(onClick = { onFavoriteToggle(pokemon) }) {
                Icon(
                    imageVector = if (pokemon.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (pokemon.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (pokemon.isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
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
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Encabezado con imagen y nombre
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
                        // Fondo circular para la imagen
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(70)
                                ),
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
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Fila con tipos de Pokémon
                        pokemon.types?.let { types ->
                            if (types.isNotEmpty()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    types.forEach { typeName -> 
                                        Chip(
                                            label = typeName,
                                            color = getTypeColor(typeName)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Información básica
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Información básica", 
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
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
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (pokemon.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Favorite status",
                                tint = if (pokemon.isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (pokemon.isFavorite) "Favorito" else "No es favorito",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                // Habilidades
                pokemon.abilities?.let { abilities ->
                    if (abilities.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Habilidades", 
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                
                                Column(
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    abilities.forEach { abilityName ->
                                        Text(
                                            text = "• $abilityName",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Estadísticas
                pokemon.stats?.let { stats ->
                    if (stats.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Estadísticas Base", 
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    stats.forEach { (statName, statValue) ->
                                        StatBar(
                                            statName = statName,
                                            statValue = statValue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f, fill = false))

                // Botón cerrar
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "Cerrar",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
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

@Composable
fun StatBar(statName: String, statValue: Int, maxValue: Int = 255) {
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
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = statValue.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Barra de progreso
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            // Calculamos el porcentaje del valor respecto al máximo
            val percentage = (statValue.toFloat() / maxValue).coerceIn(0f, 1f)
            
            // Color de la barra según el valor
            val barColor = when {
                statValue < 50 -> Color(0xFFFF5252) // Rojo para valores bajos
                statValue < 100 -> Color(0xFFFFB74D) // Naranja para valores medios
                else -> Color(0xFF66BB6A) // Verde para valores altos
            }
            
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage)
                    .background(
                        color = barColor,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
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