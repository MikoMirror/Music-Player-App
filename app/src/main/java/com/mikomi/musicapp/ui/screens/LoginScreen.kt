package com.mikomi.musicapp.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import cafe.adriel.voyager.core.screen.Screen
import com.mikomi.musicapp.viewmodel.AuthViewModel

class LoginScreen(private val authViewModel: AuthViewModel) : Screen {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        LoginScreenContent(
            onGoogleSignInClick = {
                authViewModel.signInWithGoogle(context as Activity)
            }
        )
    }
}

@Composable
fun LoginScreenContent(onGoogleSignInClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Music App", fontSize = 24.sp, modifier = Modifier.padding(bottom = 32.dp))

        Button(
            onClick = onGoogleSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Google Icon",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )
            Text("Sign in with Google")
        }
    }
}
