package com.example.goldameirl.view

import android.content.SharedPreferences
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.*
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.goldameirl.R
import com.example.goldameirl.databinding.ActivityMainBinding
import com.example.goldameirl.model.BranchManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import java.lang.Exception

class MainActivity : AppCompatActivity(), PermissionsListener {

    var permissionsManager = PermissionsManager(this)

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var locationEngine: LocationEngine
    private val callback = LocationChangeListeningCallback() //locationChangeCallback

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location>
        get() = _location

    private lateinit var branchManager: BranchManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        branchManager = BranchManager(applicationContext)
        val preferences = PreferenceManager.getDefaultSharedPreferences(application) // initialize function
        branchManager.apply {  //let the class do that
            notificationTime = preferences.getLong(NOTIFICATION_TIME, 0L)
            lastBranch = preferences.getDouble(LAST_BRANCH, 1.0)
            interval = preferences.getInt("time", 1).times(5).toLong()
            radius = preferences.getInt("radius", 5).times(100)
        }

        if (!PermissionsManager.areLocationPermissionsGranted(application)) {
            permissionsManager.requestLocationPermissions(this)
        }
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main)
        navController = this.findNavController(R.id.nav_host_fragment)
        findViewById<BottomNavigationView>(R.id.bottom_nav)
            .setupWithNavController(navController)
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
                _location.value = newLocation // make interface which is used for location change success
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

    private fun SharedPreferences.getDouble(key: String, default: Double) =
        java.lang.Double.longBitsToDouble(getLong(key, java.lang.Double.doubleToRawLongBits(default)))
}
