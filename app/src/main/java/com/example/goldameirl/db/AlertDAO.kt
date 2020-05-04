package com.example.goldameirl.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.goldameirl.model.Alert

@Dao
interface AlertDAO {
    @Insert
    fun insert(alert: Alert)

    @Query("SELECT * FROM Alerts ORDER BY id DESC LIMIT 1")
    fun getLastAlert(): Alert?

    @Query("SELECT * FROM Alerts")
    fun getAll(): LiveData<List<Alert>>

    @Update
    fun update(alert: Alert)

    @Query("DELETE FROM Alerts")
    fun clear()
}