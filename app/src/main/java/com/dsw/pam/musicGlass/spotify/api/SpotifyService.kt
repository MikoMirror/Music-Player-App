package com.dsw.pam.musicGlass.spotify.api

import retrofit2.http.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface SpotifyService {
    /**
     * Gets the current user's Spotify profile
     * Used in: MainScreen to display user information
     * @param token OAuth token for authentication
     * @return SpotifyUser object containing user details (name, email, profile picture)
     */
    @GET("me")
    suspend fun getCurrentUserProfile(
        @Header("Authorization") token: String
    ): SpotifyUser
    /**
     * Gets detailed information about a specific track
     * Used in: When displaying track details or adding to playlist
     * @param token OAuth token for authentication
     * @param trackId Spotify ID of the track
     * @return Track object with full track information
     */
    @GET("tracks/{id}")
    suspend fun getTrack(
        @Header("Authorization") token: String,
        @Path("id") trackId: String
    ): Track
    /**
     * Creates a new playlist for the user
     * Used in: When user wants to create a new playlist
     * @param token OAuth token for authentication
     * @param userId Spotify user ID who will own the playlist
     * @param playlist Request body containing name, description, and privacy setting
     * @return Created Playlist object
     */
    @POST("users/{userId}/playlists")
    suspend fun createPlaylist(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Body playlist: CreatePlaylistRequest
    ): Playlist
    /**
     * Gets the current user's playlists
     * Used in: PlaylistScreen to display user's playlists
     * @param token OAuth token for authentication
     * @param limit Maximum number of playlists to return (default: 50)
     * @return PlaylistsResponse containing list of user's playlists
     */
    @GET("me/playlists")
    suspend fun getUserPlaylists(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 50
    ): PlaylistsResponse

    companion object {
        fun create(): SpotifyService {
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

            return retrofit.create(SpotifyService::class.java)
        }
    }
}

data class SpotifyUser(
    val id: String,
    val email: String,
    val display_name: String,
    val images: List<SpotifyImage>?
)

data class TopItemsResponse(
    val items: List<Track>,
    val total: Int,
    val limit: Int,
    val offset: Int
)

data class Track(
    val id: String,
    val name: String,
    val artists: List<Artist>,
    val album: Album,
    val duration_ms: Long,
    val popularity: Int,
    val preview_url: String?
)

data class Artist(
    val id: String,
    val name: String
)

data class Album(
    val id: String,
    val name: String,
    val images: List<SpotifyImage>
)

data class CreatePlaylistRequest(
    val name: String,
    val description: String?,
    val public: Boolean
)
