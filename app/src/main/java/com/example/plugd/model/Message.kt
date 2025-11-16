package com.example.plugd.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(
    val id: String = "",
    val channelId: String = "",
    val senderId: String = "",
    val senderName: String? = null,
    val senderProfileUrl: String? = null,

    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),

    // Reply support
    val replyToMessageId: String? = null,
    val replyToSnippet: String? = null,

    // Attachments
    val mediaUrl: String? = null,
    val mediaType: String? = null,

    // Reactions
    val reactions: Map<String, Long> = emptyMap(),
    val reactors: Map<String, String> = emptyMap()
)
