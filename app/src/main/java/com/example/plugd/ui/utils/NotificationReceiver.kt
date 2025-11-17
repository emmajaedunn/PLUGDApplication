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
        val helper = NotificationHelper(context)

        // Master switch: if user turned notifications off, do nothing
        if (!helper.isNotificationsEnabled()) return

        val channelId = NotificationHelper.MAIN_CHANNEL_ID
        val notificationId = 101

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "PLUGD Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General PLUGD notifications and reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.plugd_icon)
            .setContentTitle("Stay connected with PLUGD 🔌")
            .setContentText("Check your PLUGD updates and community activity.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
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