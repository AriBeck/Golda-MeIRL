package com.example.goldameirl.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.URL


class BranchManager private constructor (context: Context) {
    private val repository = BranchRepository(context)

    val repoJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + repoJob)
    val branches: LiveData<List<Branch>> = repository.branches

    init {
        coroutineScope.launch {
            repository.refreshBranches()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: BranchManager? = null

        fun getInstance(context: Context): BranchManager? {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = BranchManager(context)
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}