package com.example.goldameirl.viewmodel

import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.goldameirl.model.BranchManager
import com.example.goldameirl.model.BranchRepository
import com.mapbox.android.core.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class MapViewModel(
    private val context: Context
) : ViewModel() {
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val repository = BranchRepository(context)

    private lateinit var locationEngine: LocationEngine
    private val callback = LocationChangeListeningCallback()
    val branchManager: BranchManager = BranchManager(context)

    private val _toNotifications = MutableLiveData<Boolean>()
    val toNotifications: LiveData<Boolean>
        get() = _toNotifications

    private val _toSettings = MutableLiveData<Boolean>()
    val toSettings: LiveData<Boolean>
        get() = _toSettings

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location>
        get() = _location

    init {
        refreshDataFromRepository()
        initLocationEngine()
    }

    val branches = repository.branches

    private fun refreshDataFromRepository() {
        coroutineScope.launch {
            try {
                repository.refreshBranches()
            } catch (e: Exception) {}
        }
    }

    fun checkBranchDistance(location: Location?) {
        branchManager.checkBranchDistance(location, branches?.value)
    }

    fun onNotificationsClick() {
        _toNotifications.value = true
    }

    fun onNotificationsClicked() {
        _toNotifications.value = false
    }

    fun onSettingsClick() {
        _toSettings.value = true
    }

    fun onSettingsClicked() {
        _toSettings.value = false
    }

    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(context)
        val request = LocationEngineRequest
            .Builder(1000)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(5000)
            .build()
        locationEngine.requestLocationUpdates(request, callback, Looper.myLooper())
        locationEngine.getLastLocation(callback)
    }

    inner class LocationChangeListeningCallback :
        LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult?) {
            if (result?.lastLocation != null) {
            val newLocation = result.lastLocation
            checkBranchDistance(newLocation)
                _location.value = newLocation
            }
        }

        override fun onFailure(exception: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
