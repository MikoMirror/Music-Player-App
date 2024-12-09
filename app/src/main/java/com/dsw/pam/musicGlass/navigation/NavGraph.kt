package com.dsw.pam.musicGlass.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import com.dsw.pam.musicGlass.ui.screens.LoginScreen
import com.dsw.pam.musicGlass.ui.screens.MainScreen
import com.dsw.pam.musicGlass.ui.screens.RegistrationScreen
import com.dsw.pam.musicGlass.viewmodels.LoginViewModel
import com.dsw.pam.musicGlass.viewmodels.RegistrationViewModel
import kotlinx.serialization.Serializable


@Serializable
sealed class Screen {
    @Serializable
    object Login : Screen()
    @Serializable
    object Register : Screen()
    @Serializable
    data class Main(val email: String) : Screen()
    companion object { fun mainRoute(email: String) = "main/$email" }


    fun route(): String = when (this) {
        is Login -> "login"
        is Register -> "register"
        is Main -> "main/$email"
    }


}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route()) {
        composable(Screen.Login.route()) {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { email ->
                    navController.navigate(Screen.mainRoute(email)) {
                        popUpTo(Screen.Login.route()) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route())
                }
            )
        }

        composable(Screen.Register.route()) {
            val registrationViewModel: RegistrationViewModel = viewModel()
            RegistrationScreen(
                viewModel = registrationViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route()) {
                        popUpTo(Screen.Register.route()) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "main/{email}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            MainScreen(email = email)
        }
    }
} 