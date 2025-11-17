package com.example.plugd.remote.api.spotify

class SpotifyRepository {

    // Fetch user's playlists
    suspend fun fetchUserPlaylists(): List<SpotifyPlaylist> {
        val token = SpotifyTokenManager.accessToken ?: return emptyList()
        val resp = SpotifyRetrofit.api.getMyPlaylists("Bearer $token")
        if (!resp.isSuccessful) return emptyList()
        return resp.body()?.items ?: emptyList()
    }
}