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
    val app = getApplication<Application>()
    val alerts = AlertManager.getInstance(app)?.alerts

    val shareClick: (Alert) -> Unit = { item ->
        val shareIntent = Intent()
        shareIntent.apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                app.getString(R.string.share_message) +
                        "${item.content} " + app.getString(R.string.at) + "${item.title}!"
            )
            type = PLAIN_TEXT
        }

        app.startActivity(shareIntent)
    }

    val deleteClick: (Alert) -> Unit = { item ->
        AlertManager.getInstance(app)?.delete(item)
    }

    val checkboxClick: (View, Alert) -> Unit = {checkbox, item ->
        if (checkbox is CheckBox) {
            item.isRead = checkbox.isChecked
            AlertManager.getInstance(app)?.update(item)
        }
    }
}
