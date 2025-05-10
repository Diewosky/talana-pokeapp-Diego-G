package com.example.talana_poke_app.presentation.navigation
 
sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object MainMenu : Screen("main_menu_screen")
    object PokemonList : Screen("pokemon_list_screen") // We'll append arguments to this route
    object Stats : Screen("stats_screen")
} 