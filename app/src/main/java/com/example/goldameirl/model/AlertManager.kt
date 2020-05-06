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
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

const val DEFAULT_RADIUS = 500
const val DEFAULT_INTERVAL = 5L
const val DEFAULT_ALERT_TIME = 0L

const val INTERVAL_KEY = "time"
const val RADIUS_KEY = "radius"

class AlertManager private constructor (val application: Context):
    LocationChangeSuccessWorker, SharedPreferences.OnSharedPreferenceChangeListener {
    var interval: Long? = 5L
    var radius: Int? = DEFAULT_RADIUS
    private var alertTime: Long? = DEFAULT_ALERT_TIME

    val branches: LiveData<List<Branch>>? = BranchManager.getInstance(application)?.branches
    val alerts = DB.getInstance(application)?.alertDAO?.getAll()

    var notificationHandler: NotificationHandler =
        AlertNotificationHandler(application,
            ALERT_CHANNEL_ID, ALERT_GROUP_ID, R.drawable.icon_branch,
            application.getString(R.string.alert_channel_description),
            application.getString(R.string.alert_channel_name)
        )

    var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    init {
        interval = preferences.getInt(INTERVAL_KEY, 1).times(5).toLong()
        radius = preferences.getInt(RADIUS_KEY, 5).times(100)
        preferences.registerOnSharedPreferenceChangeListener(this)
        LocationTool.getInstance(application)?.subscribe(this)
    }

    private fun sendAlertWhenNearBranchAndExceededInterval(location: Location?) {
        location ?: return

        branches?.value?.forEach { branch ->
            if (LocationTool.isLocationInRadius(location, branch.location(), radius) &&
                hasTimeSinceLastAlertExceededInterval(branch)) {
                storeAlertTime(branch)

                sendAlert(branch.name, branch.discounts)
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

    private fun sendAlert(title: String, content: String) {
        val newAlert = Alert(title = title, content = content)

        CoroutineScope(Dispatchers.Default).launch {
            insertToDB(newAlert)
            notifyAlert()
        }
    }

    private suspend fun notifyAlert() {
        withContext(Dispatchers.IO) {
            val alert = DB.getInstance(application)!!.alertDAO.getLastAlert()
            alert?.let {
                notificationHandler.createNotification(alert.title, alert.content, alert.id.toInt())
            }
        }
    }

    private suspend fun insertToDB(alert: Alert) {
        withContext(Dispatchers.IO) {
            DB.getInstance(application)!!.alertDAO.insert(alert)
        }
    }

    fun delete(alert: Alert) {
        CoroutineScope(Dispatchers.Default).launch {
            cancelNotification(alert)
            deleteFromDB(alert)
        }
    }

    private suspend fun deleteFromDB(alert: Alert) {
        withContext(Dispatchers.IO){
            DB.getInstance(application)!!.alertDAO.delete(alert)
        }
    }

    private fun cancelNotification(alert: Alert) {
        notificationHandler.cancel(alert.id.toInt())
    }

    override fun doWork(newLocation: Location) {
        sendAlertWhenNearBranchAndExceededInterval(newLocation)
    }

    companion object {
        @Volatile
        private var INSTANCE: AlertManager? = null

        fun getInstance(application: Context): AlertManager? {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = AlertManager(application)
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        interval = preferences.getInt(INTERVAL_KEY, 1).times(5).toLong()
        radius = preferences.getInt(RADIUS_KEY, 5).times(100)
    }
}