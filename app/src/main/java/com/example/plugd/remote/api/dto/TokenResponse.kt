package com.example.plugd.remote.api.dto

data class TokenResponse(
    val token: String,       // JWT or session token
    val user: UserDto        // User details
)