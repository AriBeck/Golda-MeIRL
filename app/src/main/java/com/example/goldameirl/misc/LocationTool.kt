package com.example.goldameirl.misc

import android.location.Location
import android.widget.Toast
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap

fun centerCameraOnLocation(mapboxMap: MapboxMap, location: Location) {
    mapboxMap.animateCamera(
        CameraUpdateFactory
            .newLatLngZoom(LatLng(location.latitude, location.longitude), 13.0)
    )
}