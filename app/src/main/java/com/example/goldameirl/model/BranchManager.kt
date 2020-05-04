package com.example.goldameirl.model

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class BranchManager private constructor (private val context: Context) {
    private val repository = BranchRepository(context)

    val repoJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + repoJob)
    lateinit var branches: LiveData<List<Branch>>

    init {
        coroutineScope.launch {
            repository.refreshBranches()
            branches = repository.branches
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