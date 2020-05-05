package com.example.goldameirl.location

import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.goldameirl.model.DEFAULT_RADIUS
import com.mapbox.android.core.location.*
import java.lang.Exception

class LocationTool private constructor(
    val context: Context
) {
    private val locationChangeSuccessWorkers = mutableListOf<LocationChangeSuccessWorker>()
    private lateinit var locationEngine: LocationEngine
    private val locationChangeCallback = LocationChangeListeningCallback()
    var currentLocation = Location("currentLocation")

    init {
        initLocationEngine()
    }

    companion object {
        fun isLocationInRadius(location: Location, center: Location, radius: Int?
        ): Boolean {
            return location.distanceTo(center) <= radius ?: DEFAULT_RADIUS
        }

        @Volatile
        private var INSTANCE: LocationTool? = null

        fun getInstance(context: Context):
                LocationTool? {

            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = LocationTool(context)
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(context)
        val request = LocationEngineRequest
            .Builder(1000)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(5000)
            .build()
        locationEngine.requestLocationUpdates(request, locationChangeCallback, Looper.myLooper())
        locationEngine.getLastLocation(locationChangeCallback)
    }

    fun subscribe(worker: LocationChangeSuccessWorker){
        locationChangeSuccessWorkers.add(worker)
    }

    fun unsubscribe(worker: LocationChangeSuccessWorker){
        locationChangeSuccessWorkers.remove(worker)
    }

    inner class LocationChangeListeningCallback():
        LocationEngineCallback<LocationEngineResult> {

        override fun onSuccess(result: LocationEngineResult?) {
            val newLocation = result?.lastLocation ?: return
            currentLocation = newLocation
            locationChangeSuccessWorkers.forEach {
                it.doWork(newLocation)
            }
        }

        override fun onFailure(exception: Exception) {}
    }


}
