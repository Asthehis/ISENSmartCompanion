package fr.isen.noemie.isensmartcompanion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.content.Context

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "event_channel"
        val channelName = "Event Notifications"
        val channelDescription = "Channel for event reminders"

        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        // Utilisez `context.getSystemService` pour obtenir le NotificationManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

