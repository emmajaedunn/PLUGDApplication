package com.example.plugd.remote.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.plugd.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: send token to backend
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            showNotification(it.title ?: "PLUGD", it.body ?: "New message")
        }

        // Optional: handle data payload
        if (remoteMessage.data.isNotEmpty()) {
            val chatMessage = remoteMessage.data["chatMessage"]
            chatMessage?.let {
                showNotification("New Chat Message", it)
            }
        }
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "plugd_channel"

        // Create channel on Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "PLUGD Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.plugd_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
    }
}