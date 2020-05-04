package com.example.goldameirl.model

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import com.example.goldameirl.R
import com.example.goldameirl.db.DB
import com.example.goldameirl.location.LocationChangeSuccessWorker
import com.example.goldameirl.location.LocationTool
import com.example.goldameirl.misc.*
import com.example.goldameirl.notifications.AlertNotificationHandler
import com.example.goldameirl.notifications.NotificationHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

const val DEFAULT_RADIUS = 500
const val DEFAULT_INTERVAL = 5L
const val DEFAULT_ALERT_TIME = 0L


const val INTERVAL_KEY = "time"
const val RADIUS_KEY = "radius"

class AlertManager private constructor (val context: Context):
    LocationChangeSuccessWorker {
    var interval: Long? = 5L
    var radius: Int? = DEFAULT_RADIUS
    private var alertTime: Long? = DEFAULT_ALERT_TIME

    val branches: LiveData<List<Branch>>? = BranchManager.getInstance(context)?.branches
    val alerts = DB.getInstance(context)?.alertDAO?.getAll()

    var notificationHandler: NotificationHandler =
        AlertNotificationHandler(
            context, ALERT_NOTIFICATION_ID,
            ALERT_CHANNEL_ID, ALERT_GROUP_ID, R.drawable.icon_branch,
            context.getString(R.string.alert_channel_description),
            context.getString(R.string.alert_channel_name)
        )

    var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        interval = preferences.getInt(INTERVAL_KEY, 1).times(5).toLong()
        radius = preferences.getInt(RADIUS_KEY, 5).times(100)
    }

    private fun sendAlertWhenNearBranchAndExceededInterval(location: Location?) {
        location ?: return

        branches?.value?.forEach { branch ->
            if (LocationTool.isLocationInRadius(location, branch.location(), radius) &&
                hasTimeSinceLastAlertExceededInterval(branch)) {
                storeAlertTime(branch)

                CoroutineScope(Dispatchers.Default).launch {
                    insertAlertToDB(branch.name, branch.discounts)
                }

                notifyAlert(branch.name, branch.discounts)
            }
        }
    }

    private fun storeAlertTime(branch: Branch) {
        with(preferences.edit()) {
            putLong(branch.name, System.currentTimeMillis())
            commit()
        }
    }

    private fun hasTimeSinceLastAlertExceededInterval(branch: Branch): Boolean {
        alertTime = preferences.getLong(branch.name, DEFAULT_ALERT_TIME)
        return (System.currentTimeMillis() - alertTime!!) >= TimeUnit.MILLISECONDS
            .convert(interval ?: DEFAULT_INTERVAL, TimeUnit.MINUTES)
    }

    private fun notifyAlert(title: String, content: String) {
        notificationHandler.createNotification(title, content)
    }

    private suspend fun insertAlertToDB(title: String, content: String) {
        withContext(Dispatchers.IO) {
            val newAlert = Alert(title = title, content = content)
            DB.getInstance(context)!!.alertDAO.insert(newAlert)
        }
    }

    override fun doWork(newLocation: Location) {
        sendAlertWhenNearBranchAndExceededInterval(newLocation)
    }

    companion object {
        @Volatile
        private var INSTANCE: AlertManager? = null

        fun getInstance(context: Context): AlertManager? {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = AlertManager(context)
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    private fun Branch.location(): Location {
        val branchLocation = Location("branchLocation")
        branchLocation.longitude = longitude
        branchLocation.latitude = latitude
        return branchLocation
    }
}