package com.example.goldameirl.viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.goldameirl.databinding.NotificationListItemBinding
import com.example.goldameirl.model.Notification
import com.example.goldameirl.model.db.NotificationDAO

class NotificationsViewModel(
    db: NotificationDAO
) : ViewModel() {
    val notifications = db.getAll()

    class NotificationAdapter :
        ListAdapter<Notification, NotificationAdapter.ViewHolder>(NotificationDiffCallBack()) {

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): ViewHolder {
            return ViewHolder.from(parent)
        }

        class ViewHolder private constructor(val binding: NotificationListItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(item: Notification) {
                binding.notification = item
                binding.executePendingBindings()
            }

            companion object {
                fun from(parent: ViewGroup): ViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = NotificationListItemBinding.inflate(layoutInflater, parent, false)
                    return ViewHolder(binding)
                }
            }
        }
    }

    class NotificationDiffCallBack : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }
    }
}
