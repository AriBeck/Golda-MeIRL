package com.example.goldameirl.view

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.scale
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.goldameirl.R
import com.example.goldameirl.viewmodel.MapViewModel
import com.example.goldameirl.databinding.FragmentMapBinding
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
//import com.mapbox.mapboxsdk.annotations.Icon
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
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage
import com.mapbox.mapboxsdk.style.layers.SymbolLayer

//import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions


/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment(), PermissionsListener, OnMapReadyCallback {

    private val SOURCE_ID = "SOURCE_ID";
    private val ICON_ID = "ICON_ID";
    private val LAYER_ID = "LAYER_ID";

    lateinit var mapView: MapView

    lateinit var viewModel: MapViewModel


    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap

    private lateinit var symbolManager: SymbolManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Mapbox.getInstance(this.context!!, "pk.eyJ1IjoiYXJpYmVjazUyIiwiYSI6ImNrOGZwa2ZveTAxdzQzbG4yOTl5ajZhOWgifQ.Ht_h6D6yCqdRTJohKF0nJA")
        val binding = DataBindingUtil.inflate<FragmentMapBinding>(inflater,
            R.layout.fragment_map,container,false)
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)



        return binding.root
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.Builder()
            .fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")) {

            viewModel.branches.observe(viewLifecycleOwner, Observer { branches ->
                Toast.makeText(activity, "Total branches: ${branches.size}", Toast.LENGTH_LONG).show()
                branches.forEach {branch ->
                    mapboxMap.addMarker(MarkerOptions()
                        .position(LatLng(branch.latitude, branch.longtitude))
                        .setIcon(IconFactory.getInstance(activity!!).fromResource(R.drawable.output_onlinepngtools))
                        .title(branch.name)
                        .setSnippet(branch.address))
                }
            })


            enableLocationComponent(it)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {

// Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(activity!!)
                .trackingGesturesManagement(true)
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(activity!!, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

// Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

// Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

// Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

// Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS

            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(activity)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(activity, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(activity, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show()
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
