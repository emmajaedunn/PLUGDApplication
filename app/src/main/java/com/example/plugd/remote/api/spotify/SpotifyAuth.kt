package com.example.plugd.remote.api.spotify

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object SpotifyAuthConfig {
    const val CLIENT_ID = "90806edf5fdc4928a6fcc4a3a40b5ff2"
    const val REDIRECT_URI = "plugd://spotify-callback"
    const val SCOPES = "playlist-read-private playlist-read-collaborative"
    const val AUTH_URL = "https://accounts.spotify.com/authorize"
    const val TOKEN_URL = "https://accounts.spotify.com/api/token"
}

object PkceUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun generateCodeVerifier(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateCodeChallenge(verifier: String): String {
        val bytes = verifier.toByteArray(Charsets.US_ASCII)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }
}