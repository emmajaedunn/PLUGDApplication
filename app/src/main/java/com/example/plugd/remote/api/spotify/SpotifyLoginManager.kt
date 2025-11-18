package com.example.plugd.remote.api.spotify

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent

object SpotifyLoginManager {
    var lastCodeVerifier: String? = null
}

// Start Spotify Auth
@RequiresApi(Build.VERSION_CODES.O)
fun startSpotifyAuth(context: Context) {
    val verifier = PkceUtils.generateCodeVerifier()
    SpotifyLoginManager.lastCodeVerifier = verifier
    val challenge = PkceUtils.generateCodeChallenge(verifier)

    val uri = Uri.Builder()
        .scheme("https")
        .authority("accounts.spotify.com")
        .path("authorize")
        .appendQueryParameter("client_id", SpotifyAuthConfig.CLIENT_ID)
        .appendQueryParameter("response_type", "code")
        .appendQueryParameter("redirect_uri", SpotifyAuthConfig.REDIRECT_URI)
        .appendQueryParameter("code_challenge_method", "S256")
        .appendQueryParameter("code_challenge", challenge)
        .appendQueryParameter("scope", SpotifyAuthConfig.SCOPES)
        .build()

    val customTabsIntent = CustomTabsIntent.Builder().build()
    customTabsIntent.launchUrl(context, uri)
}