package com.example.goldameirl.model

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BranchManager private constructor (application: Context) {
    private val repository = BranchRepository(application)


    val branches: LiveData<List<Branch>> = repository.branches

    init {
        CoroutineScope(Dispatchers.Default).launch {
            repository.refreshBranches()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: BranchManager? = null

        fun getInstance(application: Context): BranchManager? {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = BranchManager(application)
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}