package com.dsw.pam.musicGlass.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.dsw.pam.musicGlass.viewmodels.SpotifyViewModel
import com.dsw.pam.musicGlass.spotify.api.Playlist
import com.dsw.pam.musicGlass.spotify.api.Track
import kotlinx.coroutines.launch

@Composable
fun PlaylistScreen(
    spotifyViewModel: SpotifyViewModel,
    onBackClick: () -> Unit = {}
) {
    val playlists by spotifyViewModel.playlists.collectAsState()
    val isLoading by spotifyViewModel.isAuthenticating.collectAsState()
    val error by spotifyViewModel.error.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                "Your Playlists",
                style = MaterialTheme.typography.headlineMedium
            )
            Box(modifier = Modifier.width(48.dp))
        }

        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            error != null -> {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error
                )
            }
            playlists.isNullOrEmpty() -> {
                Text("No playlists found")
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(playlists ?: emptyList()) { playlist ->
                        ExpandablePlaylistCard(playlist = playlist, spotifyViewModel = spotifyViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandablePlaylistCard(playlist: Playlist, spotifyViewModel: SpotifyViewModel) {
    var isExpanded by remember { mutableStateOf(false) }
    var tracks by remember { mutableStateOf<List<Track>?>(null) }
    var isLoadingTracks by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { 
                isExpanded = !isExpanded
                if (isExpanded && tracks == null) {
                    isLoadingTracks = true
                    spotifyViewModel.viewModelScope.launch {
                        try {
                            tracks = spotifyViewModel.getPlaylistTracks(playlist.id)
                        } catch (e: Exception) {
                        } finally {
                            isLoadingTracks = false
                        }
                    }
                }
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (playlist.images.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(70.dp)
                    ) {
                        AsyncImage(
                            model = playlist.images[0].url,
                            contentDescription = "Playlist cover",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = playlist.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = playlist.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${playlist.tracks.total} tracks",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) 
                            Icons.Default.KeyboardArrowUp 
                        else 
                            Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) 
                            "Show less" 
                        else 
                            "Show more"
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                ) {
                    if (isLoadingTracks) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    } else {
                        tracks?.forEach { track ->
                            TrackItem(track = track)
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackItem(track: Track) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = track.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
} 