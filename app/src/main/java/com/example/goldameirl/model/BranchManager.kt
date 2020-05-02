package com.example.goldameirl.model

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import com.example.goldameirl.misc.NotificationHandler
import java.util.concurrent.TimeUnit

class BranchManager(private val context: Context) { //make new class alertmanager
    var interval: Long? = 5L
    var radius: Int? = 500
    var notificationTime: Long? = 0L
    var lastBranch: Double = 0.0
    val branches: LiveData<List<Branch>>? = BranchRepository.getInstance(context)?.branches

    private fun isBranchIn500(location: Location, branch: Branch): Boolean { //make more generic
        val branchLocation = Location("branch")
        branchLocation.latitude = branch.latitude
        branchLocation.longitude = branch.longitude
        return location.distanceTo(branchLocation) <= radius ?: 500 //constant
    }

    fun checkBranchDistance(location: Location?) { //divide up logic, change names
        location ?: return
        branches?.value?.forEach { branch ->
            if (isBranchIn500(location, branch) && (hasTimePast() || branch.id != lastBranch)) { //pair last branch and alert time
                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                notificationTime = System.currentTimeMillis()
                lastBranch = branch.id
                with(preferences.edit()) {
                    putLong("NotificationTime", notificationTime!!) // best way to store key value pairs?
                    putDouble("LastBranch", branch.id)
                    commit()
                }
                NotificationHandler(context).createNotification(branch.name, branch.discounts)
            }
        }
    }

    private fun hasTimePast(): Boolean { // hasTimeSinceLastAlertPast
        return (System.currentTimeMillis() - notificationTime!!) >= TimeUnit.MILLISECONDS
            .convert(interval ?: 5L, TimeUnit.MINUTES)
    }

    private fun SharedPreferences.Editor.putDouble(key: String, double: Double) =
        putLong(key, java.lang.Double.doubleToRawLongBits(double))
}