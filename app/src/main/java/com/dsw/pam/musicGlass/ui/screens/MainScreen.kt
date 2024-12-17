package com.dsw.pam.musicGlass.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dsw.pam.musicGlass.spotify.api.Playlist
import com.dsw.pam.musicGlass.spotify.api.Track
import com.dsw.pam.musicGlass.viewmodels.SpotifyViewModel
import com.dsw.pam.musicGlass.ui.components.TrackItem
import com.dsw.pam.musicGlass.ui.components.CreatePlaylistDialog

@Composable
fun MainScreen(
    email: String,
    onNavigateToPlaylists: () -> Unit,
    spotifyViewModel: SpotifyViewModel = viewModel()
) {
    val context = LocalContext.current
    val isAuthenticating by spotifyViewModel.isAuthenticating.collectAsState()
    val userProfile by spotifyViewModel.userProfile.collectAsState()
    val error by spotifyViewModel.error.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Welcome to Main Screen!")
            Text("Logged in as: $email")

            when {
                isAuthenticating -> {
                    CircularProgressIndicator()
                    Text("Connecting to Spotify...")
                }
                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(
                        onClick = { 
                            spotifyViewModel.resetState()
                            spotifyViewModel.authenticateSpotify(context) 
                        }
                    ) {
                        Text("Retry Spotify Connection")
                    }
                }
                userProfile == null -> {
                    Button(
                        onClick = { spotifyViewModel.authenticateSpotify(context) }
                    ) {
                        Text("Connect Spotify")
                    }
                }
                else -> {
                    Text(
                        "Connected as: ${userProfile?.display_name}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(
                        onClick = onNavigateToPlaylists
                    ) {
                        Text("View My Playlists")
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistItem(playlist: Playlist) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = playlist.description ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
