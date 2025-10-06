package com.example.plugd.ui.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.plugd.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val channelId = "plugd_reminder_channel"
        val notificationId = 101

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel if needed (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "PLUGD Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders and app notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build the actual notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Stay connected 💡")
            .setContentText("Check in on your PLUGD updates or messages!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}













/*package com.example.plugd.utils

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

}*/