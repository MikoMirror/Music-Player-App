package com.dsw.pam.musicGlass.spotify.api

data class Playlist(
    val id: String,
    val name: String,
    val description: String?,
    val images: List<SpotifyImage> = emptyList(),
    val tracks: PlaylistTracks,
    val external_urls: Map<String, String>,
    val href: String,
    val owner: PlaylistOwner,
    val public: Boolean?,
    val collaborative: Boolean,
    val uri: String
)

data class PlaylistTracks(
    val href: String,
    val total: Int
)

data class PlaylistOwner(
    val display_name: String,
    val external_urls: Map<String, String>,
    val href: String,
    val id: String,
    val type: String,
    val uri: String
)

data class SpotifyImage(
    val url: String,
    val height: Int?,
    val width: Int?
)

data class PlaylistsResponse(
    val href: String,
    val items: List<Playlist>,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int
) 
