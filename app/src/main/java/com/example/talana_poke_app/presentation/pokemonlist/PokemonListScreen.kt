package com.example.talana_poke_app.presentation.pokemonlist

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
            .clickable { onPokemonClick(pokemon) },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = "${pokemon.name} image",
                modifier = Modifier.size(64.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pokemon.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    style = MaterialTheme.typography.titleMedium
                )
            }
            IconButton(onClick = { onFavoriteToggle(pokemon) }) {
                Icon(
                    imageVector = if (pokemon.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (pokemon.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (pokemon.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun Chip(label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
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
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = "${pokemon.name} image",
                    modifier = Modifier.size(128.dp)
                )

                Text(
                    text = pokemon.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    style = MaterialTheme.typography.headlineMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    pokemon.height?.let { Text("Altura: ${it / 10.0} m", style = MaterialTheme.typography.bodyLarge) }
                    pokemon.weight?.let { Text("Peso: ${it / 10.0} kg", style = MaterialTheme.typography.bodyLarge) }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                pokemon.types?.let { types ->
                    if (types.isNotEmpty()) {
                        Text("Tipos:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.align(Alignment.Start).padding(top = 4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
                            types.forEach { typeName -> Chip(label = typeName) }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                pokemon.abilities?.let { abilities ->
                     if (abilities.isNotEmpty()) {
                        Text("Habilidades:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.align(Alignment.Start).padding(top = 4.dp))
                        Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp, bottom = 4.dp), horizontalAlignment = Alignment.Start) {
                            abilities.forEach { abilityName ->
                                Text("• $abilityName", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                pokemon.stats?.let { stats ->
                    if (stats.isNotEmpty()) {
                        Text("Estadísticas Base:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.align(Alignment.Start).padding(top = 4.dp))
                        Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp, bottom = 4.dp), horizontalAlignment = Alignment.Start) {
                            stats.forEach { (statName, statValue) ->
                                Text("• $statName: $statValue", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(
                        imageVector = if (pokemon.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite status",
                        tint = if (pokemon.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (pokemon.isFavorite) "Favorito" else "No es favorito",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.weight(1f, fill = false))

                Button(onClick = onDismissRequest, modifier = Modifier.fillMaxWidth()) {
                    Text("Cerrar")
                }
            }
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