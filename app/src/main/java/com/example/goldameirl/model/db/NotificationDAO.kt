package com.example.goldameirl.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.goldameirl.model.Notification

@Dao
interface NotificationDAO {
    @Insert
    fun insert(notification: Notification)

    @Query("SELECT * FROM Notifications ORDER BY id DESC LIMIT 1")
    fun getLastNotification(): Notification?

    @Query("SELECT * FROM Notifications")
    fun getAll(): LiveData<List<Notification>>

    @Query("DELETE FROM Notifications")
    fun clear()
}