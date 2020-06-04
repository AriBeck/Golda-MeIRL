package com.example.goldameirl.location

import android.content.Context
import android.location.Location
import android.os.Looper
import com.example.goldameirl.misc.DEFAULT_RADIUS
import com.example.goldameirl.misc.LOCATION_ENGINE_INTERVAL
import com.example.goldameirl.misc.MAX_WAIT_TIME
import com.mapbox.android.core.location.*
import java.lang.Exception

class LocationTool private constructor(
    val application: Context
) {
    private val locationChangeSuccessWorkList = mutableListOf<(Location) -> Unit>()
    private lateinit var locationEngine: LocationEngine
    private val locationChangeCallback = LocationChangeListeningCallback()
    var currentLocation = Location("currentLocation")

    init {
        initLocationEngine()
    }

    companion object {
        @Volatile
        private var INSTANCE: LocationTool? = null

        fun getInstance(application: Context):
                LocationTool? {

            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = LocationTool(application)
                    INSTANCE = instance
                }

                return instance
            }
        }

        fun isLocationInRadius(location: Location, center: Location, radius: Int?
        ): Boolean {
            return location.distanceTo(center) <= radius ?: DEFAULT_RADIUS
        }
    }

    private fun initLocationEngine() {
        val request = LocationEngineRequest
            .Builder(LOCATION_ENGINE_INTERVAL)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(MAX_WAIT_TIME)
            .build()

        locationEngine = LocationEngineProvider.getBestLocationEngine(application)
        locationEngine.requestLocationUpdates(request, locationChangeCallback, Looper.myLooper())
        locationEngine.getLastLocation(locationChangeCallback)
    }

    fun subscribe(work: (Location) -> Unit){
        locationChangeSuccessWorkList.add(work)
    }

    fun unsubscribe(work: (Location) -> Unit){
        locationChangeSuccessWorkList.remove(work)
    }

    inner class LocationChangeListeningCallback:
        LocationEngineCallback<LocationEngineResult> {

        override fun onSuccess(result: LocationEngineResult?) {
            val newLocation = result?.lastLocation ?: return
            currentLocation = newLocation

            locationChangeSuccessWorkList.forEach {
                it(newLocation)
            }
        }

        override fun onFailure(exception: Exception) {}
    }
}
