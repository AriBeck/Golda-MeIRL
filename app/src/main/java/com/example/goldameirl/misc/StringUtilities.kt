package com.example.goldameirl.misc

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.goldameirl.model.Notification
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("EEEE'\n'dd-MM-yyyy'\n'HH:mm")
        .format(systemTime).toString()
}

@BindingAdapter("dateTime")
fun TextView.setSleepDurationFormatted(item: Notification?) {
    item?.let {
        text = convertLongToDateString(it.createdAt)
    }
}