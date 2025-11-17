package com.example.plugd.remote.api.dto

data class TokenResponse(
    val token: String,
    val user: UserDto
)