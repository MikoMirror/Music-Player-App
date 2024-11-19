package com.dsw.pam.musicGlass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dsw.pam.musicGlass.ui.screens.LoginScreen
import com.dsw.pam.musicGlass.ui.screens.MainScreen
import com.dsw.pam.musicGlass.ui.screens.RegistrationScreen
import com.dsw.pam.musicGlass.ui.theme.MusicGlassTheme
import com.dsw.pam.musicGlass.viewmodels.LoginViewModel
import com.dsw.pam.musicGlass.viewmodels.RegistrationViewModel
import com.dsw.pam.musicGlass.navigation.NavGraph
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            MusicGlassTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}
