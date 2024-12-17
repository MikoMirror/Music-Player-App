package com.dsw.pam.musicGlass.spotify

import com.dsw.pam.musicGlass.spotify.api.*
import javax.inject.Inject

class SpotifyRepository @Inject constructor(
    private val spotifyService: SpotifyService
) {
    suspend fun getCurrentUserProfile(token: String) = 
        spotifyService.getCurrentUserProfile("Bearer $token")

    suspend fun getTopTracks(token: String, limit: Int = 20) =
        spotifyService.getTopItems("Bearer $token", "tracks", limit)

    suspend fun getTrack(token: String, trackId: String) =
        spotifyService.getTrack("Bearer $token", trackId)

    suspend fun createPlaylist(
        token: String, 
        userId: String, 
        name: String, 
        description: String? = null, 
        public: Boolean = false
    ) = spotifyService.createPlaylist(
        "Bearer $token",
        userId,
        CreatePlaylistRequest(name, description, public)
    )

    suspend fun getUserPlaylists(token: String, limit: Int = 50): List<Playlist> =
        spotifyService.getUserPlaylists("Bearer $token", limit).items

    suspend fun getPlaylistTracks(token: String, playlistId: String): PlaylistTracksResponse {
        return spotifyService.getPlaylistTracks("Bearer $token", playlistId)
    }
} 