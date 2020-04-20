package com.example.goldameirl.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goldameirl.model.Branch

@Dao
interface BranchDAO {
    @Query("SELECT * FROM Branches")
    fun getBranches(): LiveData<List<Branch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(branchList: List<Branch>)
}