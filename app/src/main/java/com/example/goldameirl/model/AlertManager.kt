package com.example.goldameirl.model

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.location.Location
import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import com.example.goldameirl.R
import com.example.goldameirl.db.DB
import com.example.goldameirl.location.LocationTool
import com.example.goldameirl.misc.*
import com.example.goldameirl.notifications.AlertNotificationHandler
import com.example.goldameirl.notifications.NotificationHandler
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class AlertManager private constructor (val application: Context):
    OnSharedPreferenceChangeListener {
    var interval: Long? = DEFAULT_INTERVAL
    var radius: Int? = DEFAULT_RADIUS
    private var alertTime: Long? = DEFAULT_ALERT_TIME

    val branches: LiveData<List<Branch>>? = BranchManager.getInstance(application)?.branches
    private val db = DB.getInstance(application)?.alertDAO!!
    val alerts = db.getAll()

    private val notificationHandler: NotificationHandler =
        AlertNotificationHandler(application,
            ALERT_CHANNEL_ID, ALERT_GROUP_ID, R.drawable.icon_branch,
            application.getString(R.string.alert_channel_description),
            application.getString(R.string.alert_channel_name)
        )

    private var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    private val onLocationChangeSuccess = { newLocation: Location ->
        sendAlertWhenNearBranchAndExceededInterval(newLocation)
    }

    init {
        getPreferences()
        preferences.registerOnSharedPreferenceChangeListener(this)
        LocationTool.getInstance(application)?.subscribe(onLocationChangeSuccess)
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
        val timeSinceLastAlert = System.currentTimeMillis() - alertTime!!

        val intervalMillis = TimeUnit.MILLISECONDS
            .convert(interval ?: DEFAULT_INTERVAL, TimeUnit.MINUTES)

        return timeSinceLastAlert >= intervalMillis
    }

    private fun sendAlert(title: String, content: String) {
        val newAlert = Alert(title = title, content = content)

        CoroutineScope(Dispatchers.Default).launch {
            val id = insertToDB(newAlert)
            notify(id, newAlert)
        }
    }

    private suspend fun notify(id: Long?, alert: Alert) {
        withContext(Dispatchers.IO) {
            notificationHandler.createNotification(alert.title, alert.content, id!!.toInt())
        }
    }

    private suspend fun insertToDB(alert: Alert): Long? {
        return withContext(Dispatchers.IO) {
            db.insert(alert)
        }
    }

    fun delete(alert: Alert) {
        CoroutineScope(Dispatchers.Default).launch {
            notificationHandler.cancel(alert.id.toInt())

            withContext(Dispatchers.IO){
                db.delete(alert)
            }
        }
    }

    fun update(alert: Alert) {
        CoroutineScope(Dispatchers.Default).launch {
            withContext(Dispatchers.IO) {
                db.update(alert)
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        getPreferences()
    }

    private fun getPreferences() {
        interval = preferences.getInt(INTERVAL_KEY, DEFAULT_INTERVAL_PREFERENCE).toLong().times(
            INTERVAL_MULTIPLIER)

        radius = preferences.getInt(RADIUS_KEY, DEFAULT_RADIUS_PREFERENCE).times(RADIUS_MULTIPLIER)
    }
}