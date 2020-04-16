package com.example.goldameirl.misc

import android.location.Location
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap

fun centerCameraOnLocation(mapboxMap: MapboxMap, location: Location) {
    mapboxMap.animateCamera(
        CameraUpdateFactory
            .newLatLngZoom(LatLng(location.latitude, location.longitude), 14.0)
    )
}
