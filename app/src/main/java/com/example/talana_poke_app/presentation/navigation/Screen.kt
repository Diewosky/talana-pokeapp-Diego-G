package com.example.talana_poke_app.presentation.navigation
 
sealed class Screen(val route: String) {
    data object Login : Screen("login_screen")
    data object MainMenu : Screen("main_menu_screen")
    data object PokemonList : Screen("pokemon_list_screen") // We'll append arguments to this route
} 