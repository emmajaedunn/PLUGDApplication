/*package com.example.plugd.remote.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.plugd.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PlugdFirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")

        // TODO: Send token to your backend so you can target this device for notifications
        // e.g., apiService.updateFcmToken(userId, token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM", "Message: ${message.data}")

        // If message contains a notification payload, show it
        message.notification?.let {
            showNotification(it.title, it.body)
        }

        // If message contains custom data (for chat, etc.)
        if (message.data.isNotEmpty()) {
            Log.d("FCM", "Data payload: ${message.data}")

            // Example: handle chat message
            val chatMessage = message.data["chatMessage"] ?: return
            showNotification("New Chat Message", chatMessage)
        }
    }

    private fun showNotification(title: String?, body: String?) {
        val channelId = "plugd_channel"

        // Create notification channel (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "PLUGD Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // Build notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.plugd_icon) // use your app icon
            .setContentTitle(title ?: "PLUGD")
            .setContentText(body ?: "You have a new message")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Show notification
        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}*/