package com.example.goldameirl.viewmodel

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.goldameirl.R
import com.example.goldameirl.databinding.NotificationListItemBinding
import com.example.goldameirl.model.Notification
import com.example.goldameirl.model.db.DB
import kotlinx.coroutines.*

class NotificationsViewModel(
    val context: Context
) : ViewModel() {
    val notifications = DB.getInstance(context)?.notificationDAO?.getAll()

    class NotificationAdapter() : // separate class file
        ListAdapter<Notification, NotificationAdapter.ViewHolder>(NotificationDiffCallBack()) {

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): ViewHolder {
            return ViewHolder.from(parent)
        }

        class ViewHolder(val binding: NotificationListItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            private val updateJob = Job()

            fun bind(item: Notification) {
                binding.notification = item
                binding.shareButton.setOnClickListener {
                    val shareIntent = Intent()
                    shareIntent.apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT, "Hey check out this great deal!\n" +
                                    "${item.content} at ${item.title}!"
                        )
                        type = "text/plain"
                    }
                    binding.root.context.startActivity(shareIntent)
                }
                binding.checkBox.apply {
                    setOnClickListener {
                        val isChecked = binding.checkBox.isChecked
                        item.isRead = isChecked
                        CoroutineScope(Dispatchers.Main + updateJob).launch {
                            withContext(Dispatchers.IO) {
                                DB.getInstance(binding.root.context)?.notificationDAO?.update(item)
                            }
                        }
                    }
                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            binding.apply {
                                notificationCard
                                    .setCardBackgroundColor(ContextCompat
                                            .getColor(binding.root.context, R.color.primaryColor))
                                titleText
                                    .setTextColor(ContextCompat
                                        .getColor(binding.root.context, R.color.primaryTextColor))
                                contentText
                                    .setTextColor(ContextCompat
                                        .getColor(binding.root.context, R.color.primaryTextColor))
                                timeStamp
                                    .setTextColor(ContextCompat
                                        .getColor(binding.root.context, R.color.primaryTextColor))
                                shareButton.
                                    setColorFilter(ContextCompat
                                        .getColor(binding.root.context, R.color.primaryTextColor))
                            }
                        } else {
                            binding.apply {
                                notificationCard
                                    .setCardBackgroundColor(ContextCompat
                                        .getColor(binding.root.context, R.color.secondaryColor))
                                titleText
                                    .setTextColor(ContextCompat
                                        .getColor(binding.root.context, R.color.secondaryTextColor))
                                contentText
                                    .setTextColor(ContextCompat
                                        .getColor(binding.root.context, R.color.secondaryTextColor))
                                timeStamp
                                    .setTextColor(ContextCompat
                                        .getColor(binding.root.context, R.color.secondaryTextColor))
                                shareButton.
                                    setColorFilter(ContextCompat
                                        .getColor(binding.root.context, R.color.secondaryTextColor))
                            }
                        }
                    }
                }
                binding.executePendingBindings()
            }

            companion object {
                fun from(parent: ViewGroup): ViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = NotificationListItemBinding
                        .inflate(layoutInflater, parent, false)
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
