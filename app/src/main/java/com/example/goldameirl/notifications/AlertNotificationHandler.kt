package com.example.goldameirl.notifications

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.goldameirl.R
import com.example.goldameirl.misc.PLAIN_TEXT
import com.example.goldameirl.view.MainActivity

class AlertNotificationHandler(override val application: Context, override val channelID: String,
                               override val groupID: String, override val iconID: Int,
                               override val channelDescription: String,
                               override val channelName: String
) : NotificationHandler {
    private lateinit var appIntent: PendingIntent
    private lateinit var shareIntent: PendingIntent
    private var channel: NotificationChannel? = null

    override fun createNotification(title: String, content: String, id: Int) {
        if(channel == null) {
            channel =  newChannel()
        }

        initAppIntent()
        initShareIntent(content, title)

        val notification = NotificationCompat.Builder(application, channelID)
            .setSmallIcon(iconID)
            .setContentTitle(title)
            .setContentText(content)
            .setGroup(groupID)
            .setContentIntent(appIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_share, "Share", shareIntent)
            .build()

        NotificationManagerCompat.from(application).notify(id, notification)
    }

    private fun initShareIntent(content: String, title: String) {
        val intent = Intent()

        intent.apply {
            action = Intent.ACTION_SEND

            putExtra(
                Intent.EXTRA_TEXT, application.getString(R.string.share_message) +
                        "$content " + application.getString(R.string.at) + " $title"
            )

            type = PLAIN_TEXT
        }
        shareIntent = PendingIntent.getActivity(application, 0, intent, 0)
    }

    private fun initAppIntent() {
        val intent = Intent(application, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        appIntent = PendingIntent.getActivity(application, 0, intent, 0)
    }
}