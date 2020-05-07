package com.example.goldameirl.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.BitmapFactory
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.goldameirl.R
import com.example.goldameirl.location.LocationChangeSuccessWorker
import com.example.goldameirl.location.LocationTool
import com.example.goldameirl.model.Branch
import com.example.goldameirl.model.BranchManager
import com.example.goldameirl.view.DEFAULT_ZOOM
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
) : AndroidViewModel(application), OnMapReadyCallback, LocationChangeSuccessWorker {
    val app = getApplication<Application>()
    private val branchManager = BranchManager.getInstance(app)
    private lateinit var mapboxMap: MapboxMap

    private val _mapReady = MutableLiveData<Boolean>()
    val mapReady: LiveData<Boolean>
        get() = _mapReady

    private val _toAlerts = MutableLiveData<Boolean>()
    val toAlerts: LiveData<Boolean>
        get() = _toAlerts

    private var currentLocation = LocationTool.getInstance(application)?.currentLocation!!

    private var branches: List<Branch>? = null

    init {
        branchManager?.branches?.observeForever { branches ->
            this.branches = branches
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.DARK) {
            enableLocationComponent(it)

            branchManager?.anitaJson?.observeForever { anitaJson ->
                it.addImage("anita-icon",
                    BitmapFactory.decodeResource(app.resources, R.drawable.icon_anita))
                it.addSource(getAnitaSource(anitaJson))
            }
        }

        _mapReady.value = true
        _mapReady.value = false
    }

    fun removeAnitaLayer() {
        mapboxMap.getStyle {
            it.removeLayer("anita-layer")
        }
    }

    fun clearMarkers() {
        mapboxMap.clear()
    }

    private fun getAnitaSource(anitaJson: String): GeoJsonSource {
        return GeoJsonSource("anita-source", anitaJson)
    }

    fun addBranchMarkers() {
        branches?.forEach { branch ->
            mapboxMap.addMarker(
                (MarkerOptions()
                    .position(LatLng(branch.latitude, branch.longitude))
                    .setIcon(
                        IconFactory.getInstance(app)
                            .fromResource(R.drawable.icon_branch)
                    )
                    .title(branch.name)
                    .setSnippet(branch.address)
                        )
            )
        }
    }

    fun addAnitaLayer() {
        mapboxMap.getStyle {
            val anitaSymbolLayer = SymbolLayer("anita-layer", "anita-source")
            anitaSymbolLayer.setProperties(PropertyFactory.iconImage("anita-icon"))
            it.addLayer(anitaSymbolLayer)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(app)) {
            val customLocationComponentOptions = LocationComponentOptions.builder(app)
                .trackingGesturesManagement(true)
                .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(app, loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            mapboxMap.locationComponent.apply {
                activateLocationComponent(locationComponentActivationOptions)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS
            }

            LocationTool.getInstance(app)?.subscribe(this)
            centerCamera()
        }
    }

    fun centerCamera() {
        mapboxMap.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(LatLng(currentLocation.latitude, currentLocation.longitude), DEFAULT_ZOOM)
        )
    }

    override fun doWork(newLocation: Location) {
        if (newLocation.distanceTo(currentLocation) >= 100) {
            mapboxMap.locationComponent.forceLocationUpdate(newLocation)
            centerCamera()
        }

        currentLocation = newLocation
    }

    fun onAlertsClick() {
        _toAlerts.value = true
    }

    fun onAlertsClicked() {
        _toAlerts.value = false
    }

    override fun onCleared() {
        LocationTool.getInstance(app)?.unsubscribe(this)
        super.onCleared()
    }
}
