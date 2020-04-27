package com.example.goldameirl.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.goldameirl.model.BranchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class MapViewModel(
    val context: Context
) : ViewModel() {
    private val repoJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + repoJob)

    private val _toNotifications = MutableLiveData<Boolean>()
    val toNotifications: LiveData<Boolean>
        get() = _toNotifications

    private val location = Location("myLocation")

    init {
        refreshDataFromRepository()
    }

    val branches = BranchRepository.getInstance(context)?.branches

    private fun refreshDataFromRepository() {
        coroutineScope.launch {
            try {
                BranchRepository.getInstance(context)?.refreshBranches()
            } catch (e: Exception) {}
        }
    }

    fun onNotificationsClick() {
        _toNotifications.value = true
    }

    fun onNotificationsClicked() {
        _toNotifications.value = false
    }

    override fun onCleared() {
        super.onCleared()
        repoJob.cancel()
    }
}
