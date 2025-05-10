package com.example.talana_poke_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.talana_poke_app.presentation.auth.AuthViewModel
import com.example.talana_poke_app.presentation.auth.LoginScreen
import com.example.talana_poke_app.presentation.mainmenu.MainMenuScreen
import com.example.talana_poke_app.presentation.pokemonlist.PokemonListScreen
import com.example.talana_poke_app.presentation.pokemonlist.PokemonListType
import com.example.talana_poke_app.presentation.stats.StatsScreen

@Composable
fun Navigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.MainMenu.route) {
            MainMenuScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        
        composable(
            route = "${Screen.PokemonList.route}/{listType}",
            arguments = listOf(
                navArgument("listType") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val listType = backStackEntry.arguments?.getString("listType")?.let {
                try {
                    PokemonListType.valueOf(it)
                } catch (e: IllegalArgumentException) {
                    PokemonListType.ALL
                }
            } ?: PokemonListType.ALL
            
            PokemonListScreen(
                navController = navController,
                listType = listType
            )
        }
        
        composable(route = Screen.Stats.route) {
            StatsScreen(
                navController = navController
            )
        }
    }
} 