package com.example.plugd.ui.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat

class NotificationHelper(private val context: Context) {

    private val prefsName = "notification_prefs"

    private fun prefs(): SharedPreferences =
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    // Main notification toggle in Settings
    fun isNotificationsEnabled(): Boolean {
        return prefs().getBoolean("notifications_enabled", false)
    }

    // Toggle notifications in Settings
    fun toggleNotifications(isEnabled: Boolean) {
        prefs().edit().putBoolean("notifications_enabled", isEnabled).apply()

        if (isEnabled) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Show an immediate notification
                showImmediateReminder()

                // Schedule daily reminder
                scheduleDailyNotification()

                Toast.makeText(context, "Notifications enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    context,
                    "Grant notification permission to enable this feature.",
                    Toast.LENGTH_LONG
                ).show()

                // Roll back the flag
                prefs().edit().putBoolean("notifications_enabled", false).apply()
            }
        } else {
            cancelDailyNotification()
            Toast.makeText(context, "Notifications turned off", Toast.LENGTH_SHORT).show()
        }
    }

    // Channel notifications toggle in Settings
    fun isChannelNotificationsEnabled(): Boolean {
        return prefs().getBoolean("channel_notifications_enabled", true)
    }

    fun setChannelNotificationsEnabled(enabled: Boolean) {
        prefs().edit().putBoolean("channel_notifications_enabled", enabled).apply()
    }

    // Daily reminder notification
    private fun showImmediateReminder() {
        val intent = Intent(context, NotificationReceiver::class.java)
        context.sendBroadcast(intent)
    }

    // Schedule daily reminder
    private fun scheduleDailyNotification() {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intervalMillis = AlarmManager.INTERVAL_DAY

        val triggerAtMillis = System.currentTimeMillis() + intervalMillis

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            intervalMillis,
            pendingIntent
        )
    }

    // Cancel daily reminder
    private fun cancelDailyNotification() {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    // Notification channel ID
    companion object {
        const val MAIN_CHANNEL_ID = "plugd_main_channel"
        private const val DAILY_REMINDER_REQUEST_CODE = 1001
    }
}