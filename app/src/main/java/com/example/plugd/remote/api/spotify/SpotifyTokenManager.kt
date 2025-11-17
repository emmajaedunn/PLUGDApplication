package com.example.plugd.remote.api.spotify

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object SpotifyTokenManager {
    var accessToken: String? = null

    private val client = OkHttpClient()

    // Exchange authorization code for access token
    suspend fun exchangeCodeForToken(code: String, codeVerifier: String) {
        withContext(Dispatchers.IO) {
            val body = FormBody.Builder()
                .add("client_id", SpotifyAuthConfig.CLIENT_ID)
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", SpotifyAuthConfig.REDIRECT_URI)
                .add("code_verifier", codeVerifier)
                .build()

            val req = Request.Builder()
                .url(SpotifyAuthConfig.TOKEN_URL)
                .post(body)
                .build()

            val resp = client.newCall(req).execute()
            val json = resp.body?.string() ?: ""
            Log.d("SpotifyToken", json)
            val obj = JSONObject(json)
            accessToken = obj.optString("access_token", null)
        }
    }
}