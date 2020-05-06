package com.example.goldameirl.notifications


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.goldameirl.view.MainActivity

class AlertNotificationHandler(override val context: Context,
                               override val channelID: String, override val groupID: String,
                               override val iconID: Int, override val channelDescription: String,
                               override val channelName: String) :
    NotificationHandler {
    private lateinit var appIntent: PendingIntent
    private lateinit var shareIntent: PendingIntent
    private var channel: NotificationChannel? = null

    override fun createNotification(title: String, content: String, id: Int) {
        if(channel == null) {
            channel =  newChannel()
        }

        initAppIntent()
        initShareIntent(content, title)

        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(iconID)
            .setContentTitle(title)
            .setContentText(content)
            .setGroup(groupID)
            .setContentIntent(appIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_share, "Share", shareIntent)
            .build()

        NotificationManagerCompat.from(context).notify(id, notification)
    }

    private fun initShareIntent(content: String, title: String) {
        val intent = Intent()
        intent.apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT, "Hey check out this great deal!\n" +
                        "$content at $title!"
            )
            type = "text/plain"
        }
        shareIntent = PendingIntent.getActivity(context, 0, intent, 0)
    }

    private fun initAppIntent() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        appIntent = PendingIntent.getActivity(context, 0, intent, 0)
    }
}