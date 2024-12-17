package com.dsw.pam.musicGlass.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dsw.pam.musicGlass.ui.screens.LoginScreen
import com.dsw.pam.musicGlass.ui.screens.MainScreen
import com.dsw.pam.musicGlass.ui.screens.PlaylistScreen
import com.dsw.pam.musicGlass.ui.screens.RegistrationScreen
import com.dsw.pam.musicGlass.viewmodels.LoginViewModel
import com.dsw.pam.musicGlass.viewmodels.RegistrationViewModel
import com.dsw.pam.musicGlass.viewmodels.SpotifyViewModel

@Composable
fun NavGraph(
    spotifyViewModel: SpotifyViewModel,
    navController: NavHostController = rememberNavController(),
) {
    val loginViewModel: LoginViewModel = viewModel()
    val registrationViewModel: RegistrationViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { email ->
                    navController.navigate("main/$email") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable(
            "main/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            MainScreen(
                email = email,
                onNavigateToPlaylists = { navController.navigate("playlists") },
                spotifyViewModel = spotifyViewModel
            )
        }
        composable("playlists") {
            PlaylistScreen(
                spotifyViewModel = spotifyViewModel,
                onBackClick = { navController.navigateUp() }
            )
        }
        composable("register") {
            RegistrationScreen(
                viewModel = registrationViewModel,
                onNavigateToLogin = { navController.navigateUp() }
            )
        }
    }
} 