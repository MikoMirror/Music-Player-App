package com.mikomi.musicapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.material3.SnackbarHostState
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.mikomi.musicapp.ui.screens.LoginScreen
import com.mikomi.musicapp.ui.screens.MainScreen
import com.mikomi.musicapp.ui.theme.MusicAppTheme
import com.mikomi.musicapp.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicAppTheme {
                val currentUser by authViewModel.currentUser.collectAsState()
                val loginResult by authViewModel.loginResult.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                Navigator(
                    screen = LoginScreen(authViewModel)
                ) { navigator ->
                    LaunchedEffect(currentUser) {
                        if (currentUser != null) {
                            navigator.replace(MainScreen(currentUser?.displayName ?: "User", authViewModel))
                        }
                    }

                    SlideTransition(navigator) { screen ->
                        screen.Content()
                    }
                }

                LaunchedEffect(loginResult) {
                    loginResult?.let { result ->
                        if (result.isFailure) {
                            val errorMessage = when (result.exceptionOrNull()) {
                                is Exception -> "No internet connection. Please check your network settings."
                                else -> "An error occurred during sign-in. Please try again."
                            }
                            snackbarHostState.showSnackbar(errorMessage)
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AuthViewModel.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { authViewModel.handleGoogleSignInResult(it) }
            } catch (e: ApiException) {
                // Handle sign-in failure
                authViewModel.setLoginResult(Result.failure(e))
            }
        }
    }
}
