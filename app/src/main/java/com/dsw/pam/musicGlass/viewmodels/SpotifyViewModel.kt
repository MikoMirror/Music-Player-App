package com.dsw.pam.musicGlass.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dsw.pam.musicGlass.spotify.SpotifyConfig
import com.dsw.pam.musicGlass.spotify.SpotifyRepository
import com.dsw.pam.musicGlass.spotify.api.Playlist
import com.dsw.pam.musicGlass.spotify.api.SpotifyService
import com.dsw.pam.musicGlass.spotify.api.Track
import com.dsw.pam.musicGlass.spotify.api.SpotifyUser
import com.spotify.sdk.android.auth.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SpotifyViewModel : ViewModel() {
    private val spotifyService = SpotifyService.create()
    private val repository = SpotifyRepository(spotifyService)

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()
    
    private val _userProfile = MutableStateFlow<SpotifyUser?>(null)
    val userProfile: StateFlow<SpotifyUser?> = _userProfile.asStateFlow()
    
    private val _topTracks = MutableStateFlow<List<Track>?>(null)
    val topTracks: StateFlow<List<Track>?> = _topTracks.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _spotifyToken = MutableStateFlow<String?>(null)
    val spotifyToken: StateFlow<String?> = _spotifyToken.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    fun authenticateSpotify(context: Context) {
        Log.d("SpotifyAuth", "Starting authentication...")
        
        try {
            val scopeString = SpotifyConfig.SCOPES.joinToString("%20")
            val uri = Uri.parse(SpotifyConfig.AUTH_URL).buildUpon()
                .appendQueryParameter("client_id", SpotifyConfig.CLIENT_ID)
                .appendQueryParameter("response_type", "token")
                .appendQueryParameter("redirect_uri", SpotifyConfig.REDIRECT_URI)
                .appendQueryParameter("scope", scopeString)
                .build()

            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(context, uri)
            
            Log.d("SpotifyAuth", "Launched auth URL in browser")
        } catch (e: Exception) {
            Log.e("SpotifyAuth", "Error launching auth URL", e)
            _error.value = "Authentication failed: ${e.message}"
        }
    }

    fun handleAuthResponse(response: AuthorizationResponse) {
        Log.d("SpotifyAuth", "Handling auth response: ${response.type}")
        
        when (response.type) {
            AuthorizationResponse.Type.TOKEN -> {
                Log.d("SpotifyAuth", "Received token: ${response.accessToken.take(5)}...")
                _spotifyToken.value = response.accessToken
                viewModelScope.launch {
                    try {
                        _isAuthenticating.value = true
                        fetchUserProfile()
                    } catch (e: Exception) {
                        _error.value = e.message
                    } finally {
                        _isAuthenticating.value = false
                    }
                }
            }
            AuthorizationResponse.Type.ERROR -> {
                Log.e("SpotifyAuth", "Auth error: ${response.error}")
                _error.value = "Authentication failed: ${response.error}"
            }
            else -> {
                Log.e("SpotifyAuth", "Unknown auth response type")
                _error.value = "Authentication failed: Unknown error"
            }
        }
    }

    private suspend fun fetchUserProfile() {
        try {
            Log.d("SpotifyAuth", "Fetching user profile...")
            spotifyToken.value?.let { token ->
                val profile = repository.getCurrentUserProfile(token)
                _userProfile.value = profile
                Log.d("SpotifyAuth", "User profile fetched successfully: ${profile.display_name}")
                
                fetchPlaylists(token)
            }
        } catch (e: Exception) {
            Log.e("SpotifyAuth", "Error fetching user profile", e)
            throw e  // Propagate the error to be handled by handleAuthResponse
        }
    }

    private suspend fun fetchPlaylists(token: String) {
        try {
            val userPlaylists = repository.getUserPlaylists(token)
            _playlists.value = userPlaylists
            Log.d("SpotifyAuth", "Playlists fetched successfully: ${userPlaylists.size}")
        } catch (e: Exception) {
            Log.e("SpotifyAuth", "Error fetching playlists", e)
            throw e  // Propagate the error to be handled by handleAuthResponse
        }
    }

    fun fetchTopTracks() {
        viewModelScope.launch {
            try {
                _isAuthenticating.value = true
                spotifyToken.value?.let { token ->
                    val response = repository.getTopTracks(token)
                    _topTracks.value = response.items
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isAuthenticating.value = false
            }
        }
    }

    fun createPlaylist(name: String, description: String?) {
        viewModelScope.launch {
            try {
                _isAuthenticating.value = true
                spotifyToken.value?.let { token ->
                    userProfile.value?.let { profile ->
                        repository.createPlaylist(
                            token = token,
                            userId = profile.id,
                            name = name,
                            description = description
                        )
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isAuthenticating.value = false
            }
        }
    }

    fun handleError(errorMessage: String) {
        _isAuthenticating.value = false
        _error.value = errorMessage
        Log.e("SpotifyAuth", "Error: $errorMessage")
    }

    fun setToken(token: String) {
        Log.d("SpotifyAuth", "Setting token and fetching profile")
        _spotifyToken.value = token
        viewModelScope.launch {
            try {
                _isAuthenticating.value = true
                fetchUserProfile()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isAuthenticating.value = false
            }
        }
    }

    fun resetState() {
        _isAuthenticating.value = false
        _error.value = null
    }

    suspend fun getPlaylistTracks(playlistId: String): List<Track> {
        return try {
            spotifyToken.value?.let { token ->
                val response = repository.getPlaylistTracks(token, playlistId)
                response.items.map { it.track }
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("SpotifyViewModel", "Error fetching playlist tracks", e)
            emptyList()
        }
    }
} 