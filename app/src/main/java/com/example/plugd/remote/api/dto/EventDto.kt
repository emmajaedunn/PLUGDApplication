package com.example.plugd.remote.api.dto

data class EventDto(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val locationName: String
)