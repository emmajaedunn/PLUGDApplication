package com.example.plugd.model

data class User(
    val userId: String? = null,
    val name: String,
    val email: String,
    val password: String
)