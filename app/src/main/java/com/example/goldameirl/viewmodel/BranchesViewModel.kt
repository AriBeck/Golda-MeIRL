package com.example.goldameirl.viewmodel

import android.app.Application
import android.location.Location
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import com.example.goldameirl.location.LocationTool
import com.example.goldameirl.model.Branch
import com.example.goldameirl.model.BranchManager
import com.google.android.material.chip.Chip

class BranchesViewModel(application: Application):
    AndroidViewModel(application) {
    var dbBranches = BranchManager.getInstance(application)?.branches
    val branches = MediatorLiveData<List<Branch>>()
    private var currentLocation = LocationTool.getInstance(application)?.currentLocation

    init {
        branches.addSource(dbBranches!!) { result ->
            result?.let {
                branches.value = it
            }
        }
    }

    fun onChipChecked(chip: View){
        if (chip is Chip) {
            if ((chip.tag as String == "ABC") && chip.isChecked) {
                branches.value = branches.value?.sortByABC()
            }

            if ((chip.tag as String == "Location") && chip.isChecked) {
                branches.value = branches.value?.sortByLocation()
            }
        }
    }

    private fun List<Branch>.sortByABC(): List<Branch>? =
        this.sortedBy { it.name }

    private fun List<Branch>?.sortByLocation(): List<Branch>? =
        this?.sortedBy {
            currentLocation?.distanceTo(it.location())
        }

    private fun Branch.location(): Location {
        val branchLocation = Location("branchLocation")
        branchLocation.longitude = longitude
        branchLocation.latitude = latitude
        return branchLocation
    }
}
