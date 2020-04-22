package com.example.goldameirl.model

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.preference.PreferenceManager
import com.example.goldameirl.misc.NotificationHandler
import com.mapbox.android.core.location.LocationEngineProvider
import java.util.concurrent.TimeUnit

class BranchManager(private val context: Context) {
    var interval: Long? = 5L
    var radius: Int? = 500
    var notificationTime: Long? = 0L
    var lastBranch: Double = 0.0


    private fun isBranchIn500(location: Location, branch: Branch): Boolean {
        val branchLocation = Location("branch")
        branchLocation.latitude = branch.latitude
        branchLocation.longitude = branch.longitude
        return location.distanceTo(branchLocation) <= radius ?: 500
    }

    fun checkBranchDistance(location: Location?, branches: List<Branch>?) {
        location ?: return
        branches?.forEach { branch ->
            if (isBranchIn500(location, branch) && (hasTimePast()
                        || branch.id != lastBranch)) {
                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                notificationTime = System.currentTimeMillis()
                lastBranch = branch.id
                with(preferences.edit()) {
                    putLong("NotificationTime", notificationTime!!)
                    putDouble("LastBranch", branch.id)
                    commit()
                }
                NotificationHandler(context).createNotification(branch.name, branch.discounts)
            }
        }
    }

    private fun hasTimePast(): Boolean {
        return (System.currentTimeMillis() - notificationTime!!) >= TimeUnit.MILLISECONDS
            .convert(interval ?: 5L, TimeUnit.MINUTES)
    }



    private fun SharedPreferences.Editor.putDouble(key: String, double: Double) =
        putLong(key, java.lang.Double.doubleToRawLongBits(double))
}