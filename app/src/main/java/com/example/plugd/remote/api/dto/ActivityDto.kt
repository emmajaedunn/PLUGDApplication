package com.example.plugd.remote.api.dto

data class ActivityDto(
    val type: String = "",
    val fromUserId: String = "",
    val message: String = "",
    val postId: String? = null,
    val timestamp: Long = 0L
)