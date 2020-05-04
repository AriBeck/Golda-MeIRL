package com.example.goldameirl.location

import android.location.Location

interface LocationChangeSuccessWorker {
    fun doWork(newLocation: Location)
}