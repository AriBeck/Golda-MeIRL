package com.example.goldameirl.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.goldameirl.model.db.DB
import com.mapbox.mapboxsdk.maps.MapboxMap
import java.lang.IllegalArgumentException

class MapViewModelFactory(
    private val db: DB
): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel Class")
    }

}