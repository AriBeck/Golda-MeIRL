package com.example.goldameirl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.goldameirl.model.BranchManager

class MapViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val branchManager = BranchManager.getInstance(application)

    private val _toAlerts = MutableLiveData<Boolean>()
    val toAlerts: LiveData<Boolean>
        get() = _toAlerts

    val branches = branchManager?.branches

    fun onAlertsClick() {
        _toAlerts.value = true
    }

    fun onAlertsClicked() {
        _toAlerts.value = false
    }
}
