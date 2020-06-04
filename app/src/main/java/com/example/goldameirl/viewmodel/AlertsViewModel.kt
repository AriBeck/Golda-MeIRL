package com.example.goldameirl.viewmodel

import android.app.Application
import android.content.Intent
import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.AndroidViewModel
import com.example.goldameirl.R
import com.example.goldameirl.misc.PLAIN_TEXT
import com.example.goldameirl.model.Alert
import com.example.goldameirl.model.AlertManager

class AlertsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val alertManager = AlertManager.getInstance(application)
    val alerts = alertManager?.alerts

    val shareClick: (Alert) -> Unit = { item ->
        val shareIntent = Intent()

        shareIntent.apply {
            action = Intent.ACTION_SEND

            putExtra(
                Intent.EXTRA_TEXT,
                application.getString(R.string.share_message) +
                        "${item.content} " + application.getString(R.string.at) + " ${item.title}!"
            )

            type = PLAIN_TEXT
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        application.startActivity(shareIntent)
    }

    val deleteClick: (Alert) -> Unit = { item ->
        alertManager?.delete(item)
    }

    val checkboxClick: (View, Alert) -> Unit = {checkbox, item ->
        if (checkbox is CheckBox) {
            item.isRead = checkbox.isChecked
            alertManager?.update(item)
        }
    }
}
