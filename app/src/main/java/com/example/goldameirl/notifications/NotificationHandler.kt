package com.example.goldameirl.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

interface NotificationHandler {
    val application: Context
    val channelID: String
    val groupID: String
    val iconID: Int
    val channelDescription: String
    val channelName: String

    fun newChannel(): NotificationChannel? {
        var channel: NotificationChannel? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            channel = NotificationChannel(channelID, channelName, importance).apply {
                this.description = channelDescription
            }

            val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as
                    NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        return channel
    }

    fun createNotification(title: String, content: String, id: Int) {
        val notification = NotificationCompat.Builder(application, channelID)
            .setSmallIcon(iconID)
            .setContentTitle(title)
            .setContentText(content)
            .setGroup(groupID)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(application).notify(id, notification)
    }

    fun cancel(id: Int) {
        NotificationManagerCompat.from(application).cancel(id)
    }
}