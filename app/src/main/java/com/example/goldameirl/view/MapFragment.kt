package com.example.goldameirl.view

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.goldameirl.R
import com.example.goldameirl.databinding.FragmentMapBinding
import com.example.goldameirl.misc.TOKEN
import com.example.goldameirl.misc.centerCameraOnLocation
import com.example.goldameirl.model.Branch
import com.example.goldameirl.viewmodel.*
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
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
    var location: Location = Location("myLocation")
    lateinit var mapboxMap: MapboxMap
    private lateinit var mainActivity: MainActivity
    var branchList: List<Branch> = ArrayList()
    private lateinit var branchMarkerList: MutableList<Marker>

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

        mapView = binding.mapView
        binding.viewModel = viewModel

        binding.locationButton.setOnClickListener {
            centerCameraOnLocation(mapboxMap, location)
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

        binding.menuButton.setOnClickListener {
            mainActivity.binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        return binding.root
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap

        mapboxMap.setStyle(Style.DARK) {
            if (mapboxMap.markers.isEmpty()) {
                addBranchLayer()
            }
            enableLocationComponent(it)
            mainActivity.location.observe(viewLifecycleOwner, Observer<Location> { newLocation ->
                mapboxMap.locationComponent.forceLocationUpdate(newLocation)
                if (location.distanceTo(newLocation) >= 100) {
                    centerCameraOnLocation(mapboxMap, newLocation)
                }
                location = newLocation
            })
            centerCameraOnLocation(mapboxMap, location)
        }

        viewModel.branches?.observe(viewLifecycleOwner, Observer { branches ->
            branchList = branches
            addBranchLayer()
        })

        val branchToggle = mainActivity.binding.navView.menu
            .findItem(R.id.branch_layer_item).actionView.findViewById<Switch>(R.id.item_switch)
        branchToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                addBranchLayer()
            }
            else {
                branchMarkerList.forEach {
                    this.mapboxMap.removeMarker(it)
                }
            }
        }
    }

    private fun addBranchLayer() {
        branchMarkerList = ArrayList()
        branchList.forEach { branch ->
            branchMarkerList.add(
                mapboxMap.addMarker(
                    (MarkerOptions()
                        .position(LatLng(branch.latitude, branch.longitude))
                        .setIcon(IconFactory.getInstance(application)
                            .fromResource(R.drawable.icon_branch))
                        .title(branch.name)
                        .setSnippet(branch.address)
                            ))
            )
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
}
