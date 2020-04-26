package com.example.goldameirl.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class BranchesViewModelFactory(
    private val context: Context,
    private val location: Location): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BranchesViewModel::class.java)) {
            return BranchesViewModel(context, location) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel Class")
    }
}