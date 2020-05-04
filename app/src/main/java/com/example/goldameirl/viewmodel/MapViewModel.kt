package com.example.goldameirl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.goldameirl.model.BranchManager

class MapViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val context = application

    private val branchManager = BranchManager.getInstance(context)

    private val _toNotifications = MutableLiveData<Boolean>()
    val toNotifications: LiveData<Boolean>
        get() = _toNotifications

    val anitaGeoJson = MutableLiveData<String>()
    val branches = branchManager?.branches

    fun onNotificationsClick() {
        _toNotifications.value = true
    }

    fun onNotificationsClicked() {
        _toNotifications.value = false
    }

    override fun onCleared() {
        super.onCleared()
        branchManager?.repoJob?.cancel()
    }
}
