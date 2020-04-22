package com.example.goldameirl.viewmodel

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.goldameirl.databinding.NotificationListItemBinding
import com.example.goldameirl.model.Notification
import com.example.goldameirl.model.db.DB

class NotificationsViewModel(
    context: Context
) : ViewModel() {
    val notifications = DB.getInstance(context)?.notificationDAO?.getAll()

    class NotificationAdapter() :
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
                binding.shareButton.setOnClickListener {
                    val shareIntent = Intent()
                    shareIntent.apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT, "Hey check out this great deal!\n" +
                                    "${item.content} at ${item.title}!")
                        type = "text/plain"
                    }
                    binding.root.context.startActivity(Intent.createChooser(shareIntent, "Send To"))
                }
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
