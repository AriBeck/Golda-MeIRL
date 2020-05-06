package com.example.goldameirl.view

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.goldameirl.R
import com.example.goldameirl.databinding.FragmentMapBinding
import com.example.goldameirl.location.LocationChangeSuccessWorker
import com.example.goldameirl.location.LocationTool
import com.example.goldameirl.misc.TOKEN
import com.example.goldameirl.model.Branch
import com.example.goldameirl.viewmodel.MapViewModel
import com.example.goldameirl.viewmodel.MapViewModelFactory
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import java.lang.Exception
import java.net.URI

const val DEFAULT_ZOOM = 15.0

class MapFragment : Fragment(), OnMapReadyCallback, LocationChangeSuccessWorker {
    private lateinit var application: Application
    private lateinit var mainActivity: MainActivity
    lateinit var viewModel: MapViewModel
    lateinit var binding: FragmentMapBinding

    private lateinit var preferences: SharedPreferences

    private var currentLocation: Location = Location("currentLocation")

    private lateinit var mapView: MapView
    lateinit var mapboxMap: MapboxMap

    var branches: List<Branch> = ArrayList()

    private lateinit var branchToggle: Switch
    private lateinit var anitaToggle: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        mainActivity = requireActivity() as MainActivity
        application = requireActivity().application
        Mapbox.getInstance(application, TOKEN)

        binding = DataBindingUtil.inflate<FragmentMapBinding>(
            inflater, R.layout.fragment_map, container, false
        )

        viewModel = ViewModelProvider(
            this, MapViewModelFactory(
                application
            )
        ).get(MapViewModel::class.java)

        binding.viewModel = viewModel
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        observeViewModel()

        initButtons()
        initToggles()

        currentLocation = LocationTool.getInstance(application)?.currentLocation!!

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.toAlerts.observe(viewLifecycleOwner, Observer { toAlerts ->
            if (toAlerts) {
                this.findNavController().navigate(
                    MapFragmentDirections
                        .mapFragmentToAlertsFragment()
                )
                viewModel.onAlertsClicked()
            }
        })

        viewModel.branches?.observe(viewLifecycleOwner, Observer { refreshedBranches ->
            branches = refreshedBranches
        })
    }

    private fun initButtons() {
        binding.menuButton.setOnClickListener {
            mainActivity.binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.locationButton.setOnClickListener {
            centerCamera(currentLocation)
        }
    }

    private fun initToggles() {
        branchToggle = mainActivity.binding.navView.menu
            .findItem(R.id.branch_layer_item).actionView.findViewById(R.id.item_switch)

        anitaToggle = mainActivity.binding.navView.menu
            .findItem(R.id.anita_layer_item).actionView.findViewById(R.id.item_switch)

        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        branchToggle.isChecked = preferences.getBoolean("branchToggle", true)
        anitaToggle.isChecked = preferences.getBoolean("anitaToggle", true)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap

        mapboxMap.setStyle(Style.DARK) {
            addAnitaSource(it)
            enableLocationComponent(it)
        }

        setLayerToggleListeners()

        setupAnnotations()
    }

    private fun setLayerToggleListeners() {
        branchToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                addBranchMarkers()
            } else {
                clearMarkers()
            }

            preferences.edit().putBoolean("branchToggle", isChecked).apply()
        }

        anitaToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                addAnitaLayer()
            } else {
                removeAnitaLayer()
            }

            preferences.edit().putBoolean("anitaToggle", isChecked).apply()
        }
    }

    private fun removeAnitaLayer() {
        this.mapboxMap.getStyle {
            it.removeLayer("anita-layer")
        }
    }

    private fun clearMarkers() {
        this.mapboxMap.clear()
    }

    private fun addAnitaSource(style: Style) {
        try {
            val anitaGeoJsonUrl = URI("https://wow-final.firebaseio.com/anita.json")
            val anitaGeoJsonSource = GeoJsonSource("anita-source", anitaGeoJsonUrl)
            style.addSource(anitaGeoJsonSource)
        } catch (e: Exception) { }

        val icon = BitmapFactory.decodeResource(resources, R.drawable.icon_anita)
        style.addImage("anita-icon", icon)
    }

    private fun addBranchMarkers() {
        branches.forEach { branch ->
            mapboxMap.addMarker(
                (MarkerOptions()
                    .position(LatLng(branch.latitude, branch.longitude))
                    .setIcon(
                        IconFactory.getInstance(application)
                            .fromResource(R.drawable.icon_branch)
                    )
                    .title(branch.name)
                    .setSnippet(branch.address)
                        )
            )
        }
    }

    private fun addAnitaLayer() {
        mapboxMap.getStyle {
            val anitaSymbolLayer = SymbolLayer("anita-layer", "anita-source")
            anitaSymbolLayer.setProperties(PropertyFactory.iconImage("anita-icon"))
            it.addLayer(anitaSymbolLayer)
        }
    }

    private fun setupAnnotations() {
        if (branchToggle.isChecked) {
            addBranchMarkers()
        }
        if (anitaToggle.isChecked) {
            addAnitaLayer()
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

            LocationTool.getInstance(application)?.subscribe(this)
            centerCamera(currentLocation)
        }
    }

    private fun centerCamera(center: Location) {
        mapboxMap.animateCamera(CameraUpdateFactory
            .newLatLngZoom(LatLng(center.latitude, center.longitude), DEFAULT_ZOOM)
        )
    }

    override fun doWork(newLocation: Location) {
        if (newLocation.distanceTo(currentLocation) >= 100) {
            mapboxMap.locationComponent.forceLocationUpdate(newLocation)
            centerCamera(newLocation)
        }

        currentLocation = newLocation
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
        LocationTool.getInstance(application)?.unsubscribe(this)
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
