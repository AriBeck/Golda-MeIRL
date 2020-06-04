package com.example.goldameirl.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.BitmapFactory
import android.location.Location
import android.widget.CompoundButton
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.goldameirl.R
import com.example.goldameirl.location.LocationTool
import com.example.goldameirl.misc.*
import com.example.goldameirl.model.Branch
import com.example.goldameirl.model.BranchManager
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class MapViewModel(
    application: Application
) : AndroidViewModel(application), OnMapReadyCallback{
    private val branchManager = BranchManager.getInstance(application)
    private val locationTool = LocationTool.getInstance(application)
    private lateinit var mapboxMap: MapboxMap

    private val _isMapReady = MutableLiveData<Boolean>()
    val isMapReady: LiveData<Boolean>
        get() = _isMapReady

    private val _shouldNavigateToAlerts = MutableLiveData<Boolean>()
    val shouldNavigateToAlerts: LiveData<Boolean>
        get() = _shouldNavigateToAlerts

    private var currentLocation = locationTool?.currentLocation!!
    private var branches: List<Branch>? = null
    private lateinit var anitaJsonObserver: Observer<String>
    private lateinit var goldaBranchesObserver: Observer<List<Branch>>

    init {
        setJsonObservers()
    }

    private fun setJsonObservers() {
        goldaBranchesObserver = Observer { branches ->
            this.branches = branches
        }
        branchManager?.branches?.observeForever(goldaBranchesObserver)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap

        mapboxMap.setStyle(Style.DARK) {
            enableLocationComponent(it)

            anitaJsonObserver = Observer { anitaJson ->
                mapboxMap.style?.apply {
                    addImage(
                        ANITA_ICON_ID,
                        BitmapFactory.decodeResource(getApplication<Application>().resources, R.drawable.icon_anita)
                    )
                    addSource(GeoJsonSource(ANITA_SOURCE_ID, anitaJson))
                }
            }

            branchManager?.anitaJson?.observeForever(anitaJsonObserver)

            _isMapReady.value = true
        }
    }

    fun onMapLoaded() {
        _isMapReady.value = false
    }

    private fun removeLayer(layerID: String) {
        if (layerID == BRANCH_LAYER_ID) {
            removeGoldaMarkers()
        }
        else {
            mapboxMap.getStyle {
                it.removeLayer(layerID)
            }
        }
    }

    private fun removeGoldaMarkers() {
        mapboxMap.clear()
    }

    private fun addGoldaMarkers() {
        branches?.forEach { branch ->
            mapboxMap.addMarker(
                (MarkerOptions()
                    .position(LatLng(branch.latitude, branch.longitude))
                    .setIcon(
                        IconFactory.getInstance(getApplication())
                            .fromResource(R.drawable.icon_branch)
                    )
                    .title(branch.name)
                    .setSnippet(branch.address)
                        )
            )
        }
    }

    fun addLayer(layerID: String) {
        if (layerID == BRANCH_LAYER_ID) {
            addGoldaMarkers()
        } else {
            mapboxMap.getStyle {
                var symbolLayer: SymbolLayer? = null

                when (layerID) {
                    ANITA_LAYER_ID -> {
                        symbolLayer = SymbolLayer(layerID, ANITA_SOURCE_ID)
                        symbolLayer.setProperties(PropertyFactory.iconImage(ANITA_ICON_ID))
                    }
                }

                it.addLayer(symbolLayer!!)
            }
        }
    }

    fun onToggleCheckedChanged(
        toggle: CompoundButton,
        isChecked: Boolean
    ) {
        if (isChecked) {
            addLayer(toggle.tag as String)
        } else {
            removeLayer(toggle.tag as String)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(getApplication())) {
            val customLocationComponentOptions = LocationComponentOptions.builder(getApplication())
                .trackingGesturesManagement(true)
                .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(getApplication(), loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            mapboxMap.locationComponent.apply {
                activateLocationComponent(locationComponentActivationOptions)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS
            }

            locationTool?.subscribe(onLocationChangeSuccess)
            centerCamera()
        }
    }

    fun centerCamera() {
        mapboxMap.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(
                    LatLng(currentLocation.latitude, currentLocation.longitude),
                    DEFAULT_ZOOM
                )
        )
    }

    private val onLocationChangeSuccess = { newLocation: Location ->
        if (newLocation.distanceTo(currentLocation) >= CAMERA_CHANGE_DISTANCE) {
            mapboxMap.locationComponent.forceLocationUpdate(newLocation)
            centerCamera()
        }

        currentLocation = newLocation
    }

    fun navigateToAlerts() {
        _shouldNavigateToAlerts.value = true
    }

    fun doneNavigatingToAlerts() {
        _shouldNavigateToAlerts.value = false
    }

    override fun onCleared() {
        locationTool?.unsubscribe(onLocationChangeSuccess)

        branchManager?.apply {
            anitaJson.removeObserver(anitaJsonObserver)
            branches.removeObserver(goldaBranchesObserver)
        }

        super.onCleared()
    }
}
