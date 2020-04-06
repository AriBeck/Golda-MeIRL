package com.example.goldameirl.view

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.goldameirl.R
import com.example.goldameirl.databinding.FragmentMapBinding
import com.example.goldameirl.misc.centerCameraOnLocation
import com.example.goldameirl.model.db.DB
import com.example.goldameirl.viewmodel.*
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style

const val TOKEN =
    "pk.eyJ1IjoiYXJpYmVjazUyIiwiYSI6ImNrOGZwa2ZveTAxdzQzbG4yOTl5ajZhOWgifQ.Ht_h6D6yCqdRTJohKF0nJA"

const val NOTIFICATION_TIME = "NotificationTime"

class MapFragment : Fragment(), PermissionsListener, OnMapReadyCallback {

    lateinit var mapView: MapView
    lateinit var viewModel: MapViewModel
    var location: Location = Location("myLocation")

    private lateinit var locationEngine: LocationEngine
    private lateinit var callback: LocationChangeListeningCallback

    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    lateinit var mapboxMap: MapboxMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Mapbox.getInstance(this.context!!, TOKEN)

        val binding = DataBindingUtil.inflate<FragmentMapBinding>(
            inflater, R.layout.fragment_map, container, false
        )
        viewModel = ViewModelProvider(
            this, MapViewModelFactory(
                activity!!.applicationContext
            )
        ).get(MapViewModel::class.java)

        val preferences = activity!!.getSharedPreferences("pref", Context.MODE_PRIVATE)
        viewModel.notificationTime = preferences.getLong(NOTIFICATION_TIME, 0L)

        mapView = binding.mapView
        binding.viewModel = viewModel
        binding.locationButton.setOnClickListener {
            centerCameraOnLocation(mapboxMap, location)
        }

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        viewModel.toNotifications.observe(viewLifecycleOwner, Observer { toNotifications ->
            if (toNotifications) {
                this.findNavController().navigate(
                    MapFragmentDirections
                        .mapFragmentToNotificationsFragment()
                )
                viewModel.onNotificationsClicked()
            }
        })


        return binding.root
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        callback = LocationChangeListeningCallback()

        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            viewModel.branches.observe(viewLifecycleOwner, Observer { branches ->
                branches.forEach { branch ->
                    mapboxMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(branch.latitude, branch.longtitude))
                            .setIcon(IconFactory.getInstance(activity!!).fromResource(R.drawable.icon_branch))
                            .title(branch.name)
                            .setSnippet(branch.address)
                    )
                }
            })

            enableLocationComponent(it)
            centerCameraOnLocation(mapboxMap, location)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {
            val customLocationComponentOptions = LocationComponentOptions.builder(activity!!)
                .trackingGesturesManagement(true)
                .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(activity!!, loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            mapboxMap.locationComponent.apply {
                activateLocationComponent(locationComponentActivationOptions)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS
            }

            initLocationEngine()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(activity)
        }
    }

    private fun initLocationEngine() {

        locationEngine = LocationEngineProvider.getBestLocationEngine(activity!!)

        val request = LocationEngineRequest
            .Builder(1000)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(5000)
            .build()

        locationEngine.requestLocationUpdates(request, callback, Looper.getMainLooper())
        locationEngine.getLastLocation(callback)
    }

    private inner class LocationChangeListeningCallback :
        LocationEngineCallback<LocationEngineResult> {

        override fun onSuccess(result: LocationEngineResult?) {
            result?.lastLocation ?: return

            if (result.lastLocation != null) {
                location.latitude = result.lastLocation?.latitude!!
                location.longitude = result.lastLocation?.longitude!!

                if (result.lastLocation != null) {
                    mapboxMap.locationComponent.forceLocationUpdate(result.lastLocation)
                    viewModel.checkBranchDistance(location)
                }
            }
        }

        override fun onFailure(exception: Exception) {}
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(activity, R.string.user_location_permission_explanation, Toast.LENGTH_LONG)
            .show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(
                activity, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG
            ).show()
            activity?.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }


}
