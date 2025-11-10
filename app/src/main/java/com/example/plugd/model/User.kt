package com.example.plugd.model

data class User(
    val userId: String? = null, // assigned by backend
    val name: String,
    val email: String,
    val password: String
)