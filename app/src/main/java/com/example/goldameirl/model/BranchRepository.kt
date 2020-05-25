package com.example.goldameirl.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.goldameirl.db.DB
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BranchRepository(application: Context) {
    private var db = DB.getInstance(application)!!.branchDAO
    val branches = db.getAll()
    var anitaJson = MutableLiveData<String>()

    suspend fun refreshBranches() {
        withContext(Dispatchers.IO) {
            val branchList = BranchAPI.retrofitService.getBranches().await()
            db.insert(branchList)
        }
    }

    fun getGeoJson(source: String) {
        BranchAPI.geoJsonRetrofitService.getGeoJson(source).enqueue(
            object: Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    when (source) {
                        "anita" -> anitaJson.value = response.body()
                    }
                }
            })
    }
}
