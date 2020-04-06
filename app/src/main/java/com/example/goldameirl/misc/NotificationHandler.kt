package com.example.goldameirl.misc


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.goldameirl.R
import com.example.goldameirl.model.db.DB
import com.example.goldameirl.view.MainActivity
import kotlinx.coroutines.*

const val CHANNEL_ID = "com.example.goldameirl.discounts"
const val GROUP_ID = "com.example.goldameirl"
const val NOTIFICATION_ID = 1

class NotificationHandler(val context: Context) {
    private val notificationJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + notificationJob)

    fun createNotification(title: String, content: String) {
        createChannel()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_branch)
            .setContentTitle(title)
            .setContentText(content)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setGroup(GROUP_ID)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)

        val newNotification =
            com.example.goldameirl.model.Notification(title = title, content = content)

        coroutineScope.launch {
            insertNotification(newNotification)
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "Main notification channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as
                    NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private suspend fun insertNotification(
        newNotification: com.example.goldameirl.model.Notification) {

        withContext(Dispatchers.IO) {
            DB.getInstance(context)!!.notificationDAO.insert(newNotification)
        }
    }
}