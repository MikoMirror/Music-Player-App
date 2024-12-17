package com.dsw.pam.musicGlass

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.dsw.pam.musicGlass.ui.theme.MusicGlassTheme
import com.dsw.pam.musicGlass.navigation.NavGraph
import com.dsw.pam.musicGlass.spotify.SpotifyConfig
import com.dsw.pam.musicGlass.viewmodels.SpotifyViewModel
import com.google.firebase.FirebaseApp
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val spotifyViewModel: SpotifyViewModel by viewModels { SpotifyViewModel.provideFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        
        Log.d("SpotifyAuth", "MainActivity onCreate")
        handleIntent(intent)
        
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("SpotifyAuth", "onNewIntent called with data: ${intent.data}")
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let { nonNullIntent ->
            val uri = nonNullIntent.data
            Log.d("SpotifyAuth", "handleIntent - URI: $uri")
            
            if (uri?.scheme == "musicglass" && uri.host == "callback") {
                Log.d("SpotifyAuth", "Processing Spotify callback")
                try {
                    val fragment = uri.fragment
                    if (fragment != null) {
                        val params = fragment.split("&").associate { 
                            val parts = it.split("=")
                            parts[0] to parts[1]
                        }
                        
                        val accessToken = params["access_token"]
                        if (accessToken != null) {
                            Log.d("SpotifyAuth", "Token extracted, length: ${accessToken.length}")
                            spotifyViewModel.setToken(accessToken)
                        } else {
                            Log.e("SpotifyAuth", "No access token in response")
                            spotifyViewModel.handleError("No access token received")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SpotifyAuth", "Error processing callback", e)
                    spotifyViewModel.handleError("Failed to process callback: ${e.message}")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("SpotifyAuth", "onActivityResult - requestCode: $requestCode, resultCode: $resultCode")

        if (requestCode == SpotifyConfig.AUTH_TOKEN_REQUEST_CODE) {
            try {
                val response = AuthorizationClient.getResponse(resultCode, data)
                Log.d("SpotifyAuth", "Processing Spotify auth result - type: ${response.type}")
                spotifyViewModel.handleAuthResponse(response)
            } catch (e: Exception) {
                Log.e("SpotifyAuth", "Error processing activity result", e)
                spotifyViewModel.handleError("Failed to process authentication: ${e.message}")
            }
        }
    }
}
