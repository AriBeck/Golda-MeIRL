package com.example.goldameirl.view

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.*
import androidx.navigation.ui.NavigationUI
import com.example.goldameirl.R
import com.example.goldameirl.databinding.ActivityMainBinding
import com.example.goldameirl.model.BranchManager
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import java.lang.Exception

class MainActivity : AppCompatActivity(), PermissionsListener {

    var permissionsManager = PermissionsManager(this)

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

    private lateinit var locationEngine: LocationEngine
    private val callback = LocationChangeListeningCallback()

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location>
        get() = _location
    private lateinit var branchManager: BranchManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!PermissionsManager.areLocationPermissionsGranted(application)) {
            permissionsManager.requestLocationPermissions(this)
        }
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,
            R.layout.activity_main)
        navController = this.findNavController(R.id.nav_host_fragment)
        drawerLayout = binding.drawerLayout
        NavigationUI.setupWithNavController(binding.navView, navController)

        branchManager = BranchManager(applicationContext)

        initLocationEngine()
    }

    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(applicationContext)
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
                branchManager.checkBranchDistance(newLocation)
                _location.value = newLocation
            }
        }

        override fun onFailure(exception: Exception) {}
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            this.recreate()
        } else {
            Toast.makeText(
                this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
