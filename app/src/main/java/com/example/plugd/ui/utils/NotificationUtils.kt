package com.example.plugd.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.plugd.R
import com.example.plugd.model.ChatNotification

fun showChatNotification(context: Context, notification: ChatNotification) {
    val channelId = "plugd_channel"

    // Android 8+ requires channel
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            channelId,
            "PLUGD Chat",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager?.createNotificationChannel(channel)
    }

    val notif = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.plugd_icon)
        .setContentTitle(notification.title)
        .setContentText(notification.body)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    val notifId = notification.id.hashCode()

}