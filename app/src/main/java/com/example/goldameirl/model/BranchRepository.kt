package com.example.goldameirl.model

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.goldameirl.db.BranchDAO
import com.example.goldameirl.db.DB
import kotlinx.coroutines.*

class BranchRepository(application: Context) {
    private var db: BranchDAO = DB.getInstance(application)!!.branchDAO
    val branches: LiveData<List<Branch>> = db.getAll()

    suspend fun refreshBranches() {
        withContext(Dispatchers.IO) {
            val branchList = BranchAPI.retrofitService.getBranches().await()
            db.insert(branchList)
        }
    }
}
