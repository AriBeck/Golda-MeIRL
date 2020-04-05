package com.example.goldameirl.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "Notifications")
data class Notification(
    @PrimaryKey (autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)