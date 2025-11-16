package com.example.plugd.remote.api.spotify

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// --------- DATA MODELS ---------
data class SpotifyImage(val url: String, val height: Int?, val width: Int?)

data class SpotifyOwner(
    val id: String,
    val display_name: String?
)

data class SpotifyPlaylist(
    val id: String,
    val name: String,
    val images: List<SpotifyImage> = emptyList(),
    val owner: SpotifyOwner
)

data class SpotifyPlaylistResponse(
    val items: List<SpotifyPlaylist>
)

// --------- SPOTIFY API ---------
interface SpotifyApiService {

    @GET("me/playlists")
    suspend fun getMyPlaylists(
        @Header("Authorization") auth: String,
        @Query("limit") limit: Int = 5
    ): Response<SpotifyPlaylistResponse>
}

// --------- RETROFIT INSTANCE ---------
object SpotifyRetrofit {

    private val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl("https://api.spotify.com/v1/")   // ⭐ Correct base URL
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()

    val api: SpotifyApiService = retrofit.create(SpotifyApiService::class.java)
}