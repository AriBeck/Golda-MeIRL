package com.example.goldameirl.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.goldameirl.databinding.AlertListItemBinding
import com.example.goldameirl.model.Alert

class AlertAdapter(private val clickListener: AlertListener):
    ListAdapter<Alert, AlertAdapter.ViewHolder>(AlertDiffCallBack()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder(val binding: AlertListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Alert,
            clickListener: AlertListener
        ) {
            binding.apply {
                alert = item
                setClickListener(clickListener)

                checkBox.apply {
                    isChecked = item.isRead
                }

                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)

                val binding = AlertListItemBinding
                    .inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class AlertDiffCallBack : DiffUtil.ItemCallback<Alert>() {
    override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem.id == newItem.id
    }
}

class AlertListener(val shareClickListener: (Alert) -> Unit,
                    val deleteClickListener: (Alert) -> Unit,
                    val checkboxClickListener: (View, Alert) -> Unit) {

    fun onShareClick(item: Alert) = shareClickListener(item)
    fun onDeleteClick(item: Alert) = deleteClickListener(item)
    fun onCheckboxClick(checkbox: View, item: Alert) = checkboxClickListener(checkbox, item)
}