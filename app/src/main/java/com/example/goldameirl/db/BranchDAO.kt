package com.example.goldameirl.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goldameirl.model.Branch

@Dao
interface BranchDAO {
    @Query("SELECT * FROM Branches")
    fun getAll(): LiveData<List<Branch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(branchList: List<Branch>)
}