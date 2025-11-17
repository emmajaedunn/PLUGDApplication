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

        // Master notifications
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