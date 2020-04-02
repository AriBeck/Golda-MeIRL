package com.example.goldameirl.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.goldameirl.model.Branch
import com.example.goldameirl.model.BranchAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MapViewModel: ViewModel() {
    // The internal MutableLiveData String that stores the most recent response
    private val _toNotifications = MutableLiveData<Boolean>()

    // The external immutable LiveData for the response String
    val toNotifications: LiveData<Boolean>
        get() = _toNotifications

    private val _branches = MutableLiveData<List<Branch>>()
    val branches: LiveData<List<Branch>>
        get() = _branches

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getBranches()
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getBranches() {
        coroutineScope.launch {
            val getBranchesDeferred = BranchAPI.retrofitService.getBranches()
            try {
                val listResult = getBranchesDeferred.await()

                _branches.value = listResult
            } catch (e: Exception) { }
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
        viewModelJob.cancel()
    }
}
