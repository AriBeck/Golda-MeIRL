package com.example.goldameirl.view


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.goldameirl.R
import com.example.goldameirl.databinding.ActivityMainBinding
import com.example.goldameirl.model.AlertManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager

class MainActivity : AppCompatActivity(), PermissionsListener {
    private var permissionsManager = PermissionsManager(this)
    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var alertManager: AlertManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!PermissionsManager.areLocationPermissionsGranted(application)) {
            permissionsManager.requestLocationPermissions(this)
        }
        alertManager = AlertManager.getInstance(application)!!

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main)
        navController = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment)!!.findNavController()
        findViewById<BottomNavigationView>(R.id.bottom_nav)
            .setupWithNavController(navController)
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
