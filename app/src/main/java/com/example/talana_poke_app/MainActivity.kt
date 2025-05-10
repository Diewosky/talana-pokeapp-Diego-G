package com.example.talana_poke_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.talana_poke_app.presentation.auth.AuthViewModel
import com.example.talana_poke_app.presentation.auth.LoginScreen
// Import MainMenuScreen once created
import com.example.talana_poke_app.presentation.mainmenu.MainMenuScreen 
import com.example.talana_poke_app.presentation.navigation.Screen
import com.example.talana_poke_app.presentation.pokemonlist.PokemonListScreen
import com.example.talana_poke_app.presentation.pokemonlist.PokemonListType
import com.example.talana_poke_app.presentation.pokemonlist.PokemonViewModel
import com.example.talana_poke_app.presentation.stats.StatsScreen
import com.example.talana_poke_app.presentation.stats.UsageStatsViewModel
import com.example.talana_poke_app.ui.theme.TalanaPokeAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val pokemonViewModel: PokemonViewModel by viewModels() // PokemonViewModel might be scoped differently later
    private val usageStatsViewModel: UsageStatsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TalanaPokeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authState by authViewModel.uiState.collectAsState()

                    // Observar cambios en el estado de autenticación
                    LaunchedEffect(authState.currentUser) {
                        if (authState.currentUser != null) {
                            // Cargar estadísticas del usuario cuando inicia sesión
                            usageStatsViewModel.loadUserStats(authState.currentUser!!.uid)
                            pokemonViewModel.setUserId(authState.currentUser!!.uid)
                        } else {
                            // Limpiar estadísticas cuando cierra sesión
                            usageStatsViewModel.clearCurrentUserStats()
                            pokemonViewModel.setUserId(null)
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = if (authState.currentUser != null) Screen.MainMenu.route else Screen.Login.route
                    ) {
                        composable(Screen.Login.route) {
                            LoginScreen(
                                authViewModel = authViewModel,
                                onLoginSuccess = {
                                    navController.navigate(Screen.MainMenu.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        
                        composable(Screen.MainMenu.route) {
                            MainMenuScreen(
                                navController = navController,
                                authViewModel = authViewModel
                            )
                        }

                        composable(
                            route = Screen.PokemonList.route + "/{listType}",
                            arguments = listOf(navArgument("listType") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val listTypeString = backStackEntry.arguments?.getString("listType")
                            val listType = PokemonListType.valueOf(listTypeString ?: PokemonListType.ALL.name)
                            
                            PokemonListScreen(
                                navController = navController,
                                pokemonViewModel = pokemonViewModel,
                                listType = listType
                            )
                        }

                        composable(route = Screen.Stats.route) {
                            StatsScreen(
                                navController = navController,
                                usageStatsViewModel = usageStatsViewModel
                            )
                        }
                    }
                    // Observe auth state to redirect if user signs out from somewhere else
                    // or if initial check moves from loading to logged out.
                    if (authState.currentUser == null && navController.currentDestination?.route != Screen.Login.route) {
                         if (navController.currentDestination?.route != Screen.Login.route && 
                             navController.graph.startDestinationRoute == Screen.MainMenu.route) { // only if we started logged in
                            navController.navigate(Screen.Login.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        }
    }
}
// Remove old direct composable call if any was here 