package com.example.goldameirl.model

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "Branches")
data class Branch(
    @PrimaryKey val id: Double,
    val name: String,
    val address: String,
    val latitude: Double,
    @Json(name = "longtitude") val longitude: Double,
    val phone: String,
    val discounts: String
)

fun Branch.location(): Location {
    val branchLocation = Location("branchLocation")
    branchLocation.longitude = longitude
    branchLocation.latitude = latitude
    return branchLocation
}

