package com.example.goldameirl.model

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import com.example.goldameirl.misc.NotificationHandler

const val NOTIFICATION_INTERVAL = 300000L

class BranchManager(private val context: Context) {
    var notificationTime: Long? = 0L
    var lastBranch: Double = 0.0

    fun isBranchIn500(location: Location, branch: Branch): Boolean {
        val branchLocation = Location("branch")
        branchLocation.latitude = branch.latitude
        branchLocation.longitude = branch.longitude
        return location.distanceTo(branchLocation) <= 500
    }

    fun checkBranchDistance(location: Location, branches: List<Branch>?) {
        branches?.forEach { branch ->
            if (isBranchIn500(location, branch) && (hasTimePast()
                        || branch.id != lastBranch)) {
                val preferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
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
        return (System.currentTimeMillis() - notificationTime!!) >= NOTIFICATION_INTERVAL
    }

    private fun SharedPreferences.Editor.putDouble(key: String, double: Double) =
        putLong(key, java.lang.Double.doubleToRawLongBits(double))
}