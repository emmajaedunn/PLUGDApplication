package com.example.plugd.model

import java.time.Instant
import java.util.Date

data class Community(
    val communityId: String,
    val communityName: String,
    val members: List<String> = emptyList()
)

data class Post(
    val postId: String,
    val userId: String,
    val communityId: String,
    val text: String,
    val mediaUrl: String?,
    val dateTime: Date
)