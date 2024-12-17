package com.dsw.pam.musicGlass.spotify

import com.spotify.sdk.android.auth.AuthorizationResponse

object SpotifyConfig {
    const val CLIENT_ID = "8638a99b5818430ab90e77e31c190867"
    const val REDIRECT_URI = "musicglass://callback"
    const val AUTH_TOKEN_REQUEST_CODE = 0x10
    
    const val AUTH_URL = "https://accounts.spotify.com/authorize"
    
    val SCOPES = arrayOf(
        "user-read-private",
        "user-read-email",
        "user-top-read",
        "playlist-modify-public",
        "playlist-modify-private"
    )
} 