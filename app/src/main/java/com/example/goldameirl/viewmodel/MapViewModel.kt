package com.example.goldameirl.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.goldameirl.misc.NotificationHandler
import com.example.goldameirl.model.Branch
import com.example.goldameirl.model.BranchManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val NOTIFICATION_INTERVAL = 300000L

class MapViewModel(
    private val context: Context
) : ViewModel() {
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _toNotifications = MutableLiveData<Boolean>()
    val toNotifications: LiveData<Boolean>
        get() = _toNotifications

    val branchManager: BranchManager = BranchManager()

    private val _branches = MutableLiveData<List<Branch>>()
    val branches: LiveData<List<Branch>>
        get() = _branches

    var notificationTime: Long? = 0L

    init {
        coroutineScope.launch {
            _branches.value = branchManager.getBranches()
        }
    }

    fun checkBranchDistance(location: Location) {
        branches.value?.forEach { branch ->
            if (branchManager.isBranchIn500(location, branch) && hasTimePast()) {
                val preferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
                notificationTime = System.currentTimeMillis()
                with(preferences.edit()) {
                    putLong("NotificationTime", notificationTime!!)
                    commit()
                }
                NotificationHandler(context).createNotification(branch.name, branch.discounts)
            }
        }
    }

    private fun hasTimePast(): Boolean {
        return (System.currentTimeMillis() - notificationTime!!) >= NOTIFICATION_INTERVAL
    }

    fun onNotificationsClick() {
        _toNotifications.value = true
    }

    fun onNotificationsClicked() {
        _toNotifications.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
