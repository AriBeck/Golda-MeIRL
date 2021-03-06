package com.example.goldameirl.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.goldameirl.location.LocationTool
import com.example.goldameirl.misc.ABC_TAG
import com.example.goldameirl.misc.LOCATION_TAG
import com.example.goldameirl.model.Branch
import com.example.goldameirl.model.BranchManager
import com.example.goldameirl.model.location
import com.google.android.material.chip.Chip

class BranchesViewModel(application: Application):
    AndroidViewModel(application) {
    var branches = MutableLiveData<List<Branch>>()
    private var currentLocation = LocationTool.getInstance(application)?.currentLocation

    init {
        branches.value = BranchManager.getInstance(application)!!.branches.value
    }

    fun onChipChecked(chip: View) {
        if (chip is Chip && chip.isChecked) {
            when (chip.tag as String) {
                ABC_TAG -> branches.value = branches.value?.sortByABC()
                LOCATION_TAG -> branches.value = branches.value?.sortByLocation()
            }
        }
    }

    private fun List<Branch>.sortByABC(): List<Branch>? =
        this.sortedBy { it.name }

    private fun List<Branch>?.sortByLocation(): List<Branch>? =
        this?.sortedBy {
            currentLocation?.distanceTo(it.location())
        }

    val phoneClick: (Branch) -> Unit =  { item ->
            val phoneIntent = Intent()

            phoneIntent.apply {
                action = Intent.ACTION_DIAL
                data = Uri.parse("tel:${item.phone}")
            }

            if (phoneIntent.resolveActivity(application.packageManager) != null){
                application.startActivity(phoneIntent)
            }
    }
}
