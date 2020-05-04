package com.example.goldameirl.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.goldameirl.model.AlertManager

class AlertsViewModel(
    val context: Context
) : ViewModel() {
    val alerts = AlertManager.getInstance(context)?.alerts
}
