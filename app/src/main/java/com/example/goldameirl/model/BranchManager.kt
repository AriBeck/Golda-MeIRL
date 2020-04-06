package com.example.goldameirl.model

import android.location.Location
import kotlinx.coroutines.*
import java.lang.Exception

class BranchManager() {

    fun isBranchIn500(location: Location, branch: Branch): Boolean {
        val branchLocation = Location("branch")
        branchLocation.latitude = branch.latitude
        branchLocation.longitude = branch.longtitude
        return location.distanceTo(branchLocation) <= 500
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