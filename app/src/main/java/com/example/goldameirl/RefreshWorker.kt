package com.example.goldameirl

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.goldameirl.model.BranchRepository
import retrofit2.HttpException

class RefreshWorker(application: Context, params: WorkerParameters) :
    CoroutineWorker(application, params) {

    companion object {
        const val WORK_NAME = "com.example.goldameirl.RefreshWorker"
    }

    override suspend fun doWork(): Result {
        val repository = BranchRepository(applicationContext)

        try {
            repository.refreshBranches()
            Log.d("RefreshWorker", "Refresh request run")
        } catch (e: HttpException) {
            return Result.retry()
        }

        return Result.success()
    }
}