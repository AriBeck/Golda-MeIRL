package com.example.goldameirl.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Alerts")
data class Alert(
    @PrimaryKey (autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    var isRead: Boolean = false
)