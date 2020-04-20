package com.example.goldameirl.model

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.goldameirl.model.db.BranchDAO
import com.example.goldameirl.model.db.DB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BranchRepository(context: Context) {
    private var db: BranchDAO = DB.getInstance(context)!!.branchDAO
    val branches: LiveData<List<Branch>>? = db.getBranches()

    suspend fun refreshBranches(){
        withContext(Dispatchers.IO) {
            val branchList = BranchAPI.retrofitService.getBranches().await()
            db.insertList(branchList)
        }
    }
}