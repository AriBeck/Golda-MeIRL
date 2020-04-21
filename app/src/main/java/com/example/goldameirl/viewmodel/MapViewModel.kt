package com.example.goldameirl.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.goldameirl.model.BranchManager
import com.example.goldameirl.model.BranchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class MapViewModel(
    context: Context
) : ViewModel() {
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val repository = BranchRepository(context)
    val branchManager: BranchManager = BranchManager(context)

    private val _toNotifications = MutableLiveData<Boolean>()
    val toNotifications: LiveData<Boolean>
        get() = _toNotifications

    private val _toSettings = MutableLiveData<Boolean>()
    val toSettings: LiveData<Boolean>
        get() = _toSettings

    init {
        refreshDataFromRepository()
    }

    val branches = repository.branches

    private fun refreshDataFromRepository() {
        coroutineScope.launch {
            try {
                repository.refreshBranches()
            } catch (e: Exception) {}
        }
    }

    fun checkBranchDistance(location: Location) {
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
