package com.example.goldameirl.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.goldameirl.model.db.DB
import com.example.goldameirl.model.db.NotificationDAO
import java.lang.IllegalArgumentException

class NotificationsViewModelFactory(
    private val db: NotificationDAO): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
            return NotificationsViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel Class")
    }

}