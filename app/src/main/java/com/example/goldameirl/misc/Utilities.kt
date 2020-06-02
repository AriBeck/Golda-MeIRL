package com.example.goldameirl.misc

import android.annotation.SuppressLint
import android.telephony.PhoneNumberUtils
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun convertToDate(systemTime: Long): String {
    return SimpleDateFormat("dd-MM-yyyy'\n'HH:mm")
        .format(systemTime).toString()
}

@BindingAdapter("dateTime")
fun TextView.formatDateTime(time: Long?) {
    time?.let {
        text = convertToDate(time)
    }
}

@BindingAdapter("phone")
fun TextView.formatPhone(number: String?) {
    number?.let {
        text = PhoneNumberUtils.formatNumber(it)
    }
}