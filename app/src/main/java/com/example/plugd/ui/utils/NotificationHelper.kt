package com.example.plugd.ui.utils

import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat

class NotificationHelper(private val context: Context) {

    private val prefsName = "notification_prefs"
    private val notificationPermissionCode = 101

    fun isNotificationsEnabled(): Boolean {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        return prefs.getBoolean("notifications_enabled", false)
    }

    fun toggleNotifications(isEnabled: Boolean) {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        prefs.edit().putBoolean("notifications_enabled", isEnabled).apply()

        if (isEnabled) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                scheduleRepeatingNotification()
                Toast.makeText(context, "Notifications enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Grant notification permission to enable this feature.", Toast.LENGTH_LONG).show()
                toggleNotifications(false)
            }
        } else {
            cancelRepeatingNotification()
            Toast.makeText(context, "Notifications turned off", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scheduleRepeatingNotification() {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intervalMillis = 6 * 60 * 60 * 1000L // every 6 hours
        val startTime = System.currentTimeMillis() + 5000L

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startTime,
            intervalMillis,
            pendingIntent
        )
    }

    private fun cancelRepeatingNotification() {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}