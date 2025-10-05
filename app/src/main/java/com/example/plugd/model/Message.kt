package com.example.plugd.model

data class Message(
    val id: String = "",
    val channelId: String = "",
    val senderId: String = "",
    val content: String = "",
    val senderName: String = "",
    val timestamp: Long = 0L
)