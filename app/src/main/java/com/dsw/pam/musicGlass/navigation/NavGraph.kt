package com.dsw.pam.musicGlass.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dsw.pam.musicGlass.ui.screens.LoginScreen
import com.dsw.pam.musicGlass.ui.screens.MainScreen
import com.dsw.pam.musicGlass.ui.screens.RegistrationScreen
import com.dsw.pam.musicGlass.viewmodels.LoginViewModel
import com.dsw.pam.musicGlass.viewmodels.RegistrationViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main/{email}") {
        fun createRoute(email: String) = "main/$email"
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { email ->
                    navController.navigate(Screen.Main.createRoute(email)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            val registrationViewModel: RegistrationViewModel = viewModel()
            RegistrationScreen(
                viewModel = registrationViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Main.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            MainScreen(email = email)
        }
    }
} 