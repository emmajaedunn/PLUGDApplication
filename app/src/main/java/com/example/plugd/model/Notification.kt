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
    val id: String,           // Unique ID for the notification (e.g., message ID)
    val title: String,        // Notification title
    val body: String,         // Notification content
    val channelId: String     // Optional: channel for grouping (not the Android channel, just your app logic)
)