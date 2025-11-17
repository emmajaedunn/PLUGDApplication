package com.example.plugd.model
enum class NotificationType { EVENT_REMINDER, NEW_FOLLOWER, GROUP_POST }

data class AppNotification(
    val notificationId: String,
    val userId: String,
    val message: String,
    val type: NotificationType,
    val readStatus: Boolean
)

data class ChatNotification(
    val id: String,
    val title: String,
    val body: String,
    val channelId: String
)