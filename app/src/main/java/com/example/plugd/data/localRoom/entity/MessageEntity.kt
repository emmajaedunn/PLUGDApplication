package com.example.plugd.data.localRoom.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey val channelId: String = UUID.randomUUID().toString(),
    val name: String,
    val lastMessage: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val messageId: String = UUID.randomUUID().toString(),
    val channelId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long
)