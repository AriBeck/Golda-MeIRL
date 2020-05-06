package com.example.goldameirl.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

interface NotificationHandler {
    val context: Context
    val channelID: String
    val groupID: String
    val iconID: Int
    val channelDescription: String
    val channelName: String

    fun newChannel (): NotificationChannel? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID, channelName, importance).apply {
                this.description = channelDescription
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as
                    NotificationManager
            notificationManager.createNotificationChannel(channel)
            return channel
        }
        return null
    }

    fun createNotification(title: String, content: String, id: Int) {
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(iconID)
            .setContentTitle(title)
            .setContentText(content)
            .setGroup(groupID)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notification)
    }
}