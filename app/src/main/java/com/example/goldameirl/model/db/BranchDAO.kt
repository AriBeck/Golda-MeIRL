package com.example.goldameirl.model.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goldameirl.model.Branch
// take db package out
@Dao
interface BranchDAO {
    @Query("SELECT * FROM Branches")
    fun getBranches(): LiveData<List<Branch>> // getAll

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(branchList: List<Branch>) // don't need list in title
}