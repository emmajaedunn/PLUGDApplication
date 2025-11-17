package com.example.plugd.remote.firebase

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.plugd.R
import com.example.plugd.ui.utils.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingServiceImpl : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val helper = NotificationHelper(applicationContext)

        // Respect master + community/channel toggle
        if (!helper.isNotificationsEnabled() || !helper.isChannelNotificationsEnabled()) {
            return
        }

        val channelId = "plugd_realtime_channel"

        createChannelIfNeeded(channelId)

        val title = message.data["title"]
            ?: message.notification?.title
            ?: "PLUGD"

        val body = message.data["body"]
            ?: message.notification?.body
            ?: "You have a new update in PLUGD"

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.plugd_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this as Context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(System.currentTimeMillis().toInt(), notification)
        }
    }

    private fun createChannelIfNeeded(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                channelId,
                "PLUGD real-time notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Follow requests, messages, new plugs, etc."
            }
            manager.createNotificationChannel(channel)
        }
    }
}