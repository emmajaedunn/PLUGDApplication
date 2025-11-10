package com.example.plugd.remote.api.dto

data class CreateEventDto(
    val name: String,
    val category: String?,
    val description: String?,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?,
    val date: Long?,            // epoch millis
    val spotifyPlaylistId: String? = null
)