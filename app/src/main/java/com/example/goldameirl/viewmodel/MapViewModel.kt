package com.example.goldameirl.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.goldameirl.model.Branch
import com.example.goldameirl.model.BranchAPI
import com.example.goldameirl.model.BranchManager
import com.example.goldameirl.model.Notification
import com.example.goldameirl.model.db.DB
import com.mapbox.mapboxsdk.maps.MapboxMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

const val NOTIFICATION_INTERVAL = 300000L

class MapViewModel(
    private val db: DB): ViewModel() {
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _toNotifications = MutableLiveData<Boolean>()
    val toNotifications: LiveData<Boolean>
        get() = _toNotifications

    val branchManager: BranchManager = BranchManager(db)

    private val _branches = MutableLiveData<List<Branch>>()
    val branches: LiveData<List<Branch>>
        get() = _branches

    private var notificationTime: Long = 0L

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        coroutineScope.launch {
            _branches.value = branchManager.getBranches()
        }
    }

    fun checkBranchDistance(location: Location) {
        branches.value?.forEach { branch ->
            if(branchManager.isBranchIn500(location, branch) && hasTimePast()){
                notificationTime = System.currentTimeMillis()
                val newNotification = Notification(title = branch.name, content = branch.discounts)
                coroutineScope.launch {
                    branchManager.insertNotification(newNotification)
                }
                // build phone notification
            }
        }
    }

    private fun hasTimePast(): Boolean {
        return (System.currentTimeMillis() - notificationTime) >= NOTIFICATION_INTERVAL
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */


    fun onNotificationsClick() {
        _toNotifications.value = true
    }

    fun onNotificationsClicked() {
        _toNotifications.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        branchManager.mainJob.cancel()
    }
}
