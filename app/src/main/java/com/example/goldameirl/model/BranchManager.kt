package com.example.goldameirl.model

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.goldameirl.model.db.DB
import com.mapbox.mapboxsdk.maps.MapboxMap
import kotlinx.coroutines.*
import java.lang.Exception

class BranchManager(
    private val db: DB
) {
    var listResult = listOf<Branch>()


    val mainJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + mainJob)

    private val _branches = MutableLiveData<List<Branch>>()
    val branches: LiveData<List<Branch>>
        get() = _branches

    fun isBranchIn500(location: Location, branch: Branch): Boolean{
        val branchLocation = Location("branch")
        branchLocation.latitude = branch.latitude
        branchLocation.longitude = branch.longtitude
        val result = location!!.distanceTo(branchLocation) <= 500
        return result
    }

    suspend fun insertNotification(newNotification: Notification) {

        withContext(Dispatchers.IO) {
          db.notificationDAO.insert(newNotification)
        }
    }

    suspend fun getBranches(): List<Branch>? {
        return withContext(Dispatchers.IO) {
            var listResult = listOf<Branch>()
            val getBranchesDeferred = BranchAPI.retrofitService.getBranches()
            try {
                listResult = getBranchesDeferred.await()
            } catch (e: Exception) { }
            listResult
        }
    }
}