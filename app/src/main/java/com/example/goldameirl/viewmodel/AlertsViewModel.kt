package com.example.goldameirl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.goldameirl.model.AlertManager

class AlertsViewModel(
    application: Application
) : AndroidViewModel(application) {
    val alerts = AlertManager.getInstance(application)?.alerts
}
