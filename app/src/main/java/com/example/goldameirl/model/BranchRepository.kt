package com.example.goldameirl.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.goldameirl.db.BranchDAO
import com.example.goldameirl.db.DB
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL

class BranchRepository(context: Context) {
    private var db: BranchDAO = DB.getInstance(context)!!.branchDAO
    val branches: LiveData<List<Branch>> = db.getAll()

    suspend fun refreshBranches() {
        withContext(Dispatchers.IO) {
            val branchList = BranchAPI.retrofitService.getBranches().await()
            db.insert(branchList)
        }
    }
}
