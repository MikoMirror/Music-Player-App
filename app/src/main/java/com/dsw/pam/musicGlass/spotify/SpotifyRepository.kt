package com.dsw.pam.musicGlass.spotify

import com.dsw.pam.musicGlass.spotify.api.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SpotifyRepository {
    private val spotifyService: SpotifyService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        spotifyService = retrofit.create(SpotifyService::class.java)
    }

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
} 