package com.example.goldameirl.misc

import android.annotation.SuppressLint
import android.telephony.PhoneNumberUtils
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.goldameirl.model.Branch
import com.example.goldameirl.model.Alert
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun convertToDate(systemTime: Long): String {
    return SimpleDateFormat("dd-MM-yyyy'\n'HH:mm")
        .format(systemTime).toString()
}

@BindingAdapter("dateTime")
fun TextView.formatDateTime(item: Alert?) {
    item?.let {
        text = convertToDate(it.createdAt)
    }
}

@BindingAdapter("phone")
fun TextView.formatPhone(item: Branch?) {
    item?.let {
        text = PhoneNumberUtils.formatNumber(it.phone)
    }
}