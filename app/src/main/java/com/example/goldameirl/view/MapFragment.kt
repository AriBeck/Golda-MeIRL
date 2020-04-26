package com.example.goldameirl.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.goldameirl.R
import com.example.goldameirl.databinding.FragmentMapBinding
import com.example.goldameirl.misc.TOKEN
import com.example.goldameirl.misc.centerCameraOnLocation
import com.example.goldameirl.viewmodel.*
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

const val NOTIFICATION_TIME = "NotificationTime"
const val LAST_BRANCH = "LastBranch"

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var application: Context
    private var mapView: MapView? = null
    lateinit var viewModel: MapViewModel
    lateinit var binding: FragmentMapBinding
    lateinit var drawerLayout: DrawerLayout
    var location: Location = Location("myLocation")
    lateinit var mapboxMap: MapboxMap
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        mainActivity = requireNotNull(activity) as MainActivity
        application = requireNotNull(activity).application
        Mapbox.getInstance(application, TOKEN)
        binding = DataBindingUtil.inflate<FragmentMapBinding>(
            inflater, R.layout.fragment_map, container, false
        )

        viewModel = ViewModelProvider(
            this, MapViewModelFactory(
                application
            )
        ).get(MapViewModel::class.java)

        val preferences = PreferenceManager.getDefaultSharedPreferences(application)
        viewModel.branchManager.apply {
            notificationTime = preferences.getLong(NOTIFICATION_TIME, 0L)
            lastBranch = preferences.getDouble(LAST_BRANCH, 1.0)
            interval = preferences.getInt("time", 1).times(5).toLong()
            radius = preferences.getInt("radius", 5).times(100)
        }
        mapView = binding.mapView
        binding.viewModel = viewModel
        drawerLayout = activity?.findViewById(R.id.drawer_layout)!!
        binding.locationButton.setOnClickListener {
            centerCameraOnLocation(mapboxMap, location)
        }
        binding.menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

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

        mapboxMap.setStyle(Style.DARK) {
            viewModel.branches?.observe(viewLifecycleOwner, Observer { branches ->
                branches.forEach { branch ->
                    mapboxMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(branch.latitude, branch.longitude))
                            .setIcon(IconFactory.getInstance(application).fromResource(R.drawable.icon_branch))
                            .title(branch.name)
                            .setSnippet(branch.address)
                    )
                }
            })
            enableLocationComponent(it)
            centerCameraOnLocation(mapboxMap, location)

            mainActivity.location.observe(viewLifecycleOwner, Observer<Location> { newLocation ->
                mapboxMap.locationComponent.forceLocationUpdate(newLocation)
                if (location.distanceTo(newLocation) >= 100) {
                    centerCameraOnLocation(mapboxMap, newLocation)
                }
                viewModel.checkBranchDistance(newLocation)
                location = newLocation
            })
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(application)) {
            val customLocationComponentOptions = LocationComponentOptions.builder(application)
                .trackingGesturesManagement(true)
                .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(application, loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            mapboxMap.locationComponent.apply {
                activateLocationComponent(locationComponentActivationOptions)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS
            }
        }
    }



    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    private fun SharedPreferences.getDouble(key: String, default: Double) =
        java.lang.Double.longBitsToDouble(getLong(key, java.lang.Double.doubleToRawLongBits(default)))
}
