package com.example.plugd.remote.api

import com.example.plugd.model.User

data class AuthResponse(
    val message: String,
    val user: User? = null
)