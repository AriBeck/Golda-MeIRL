package com.example.goldameirl.viewmodel

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.goldameirl.R
import com.example.goldameirl.databinding.AlertListItemBinding
import com.example.goldameirl.db.DB
import com.example.goldameirl.model.Alert
import kotlinx.coroutines.*

class AlertAdapter: ListAdapter<Alert, AlertAdapter.ViewHolder>(AlertDiffCallBack()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder(val binding: AlertListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val updateJob = Job()

        fun bind(item: Alert) {
            binding.alert = item
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
                            DB.getInstance(binding.root.context)?.alertDAO?.update(item)
                        }
                    }
                }
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        setAsRead()
                    } else {
                        setAsNotRead()
                    }
                }
            }
            binding.executePendingBindings()
        }

        private fun setAsNotRead() {
            binding.apply {
                notificationCard
                    .setCardBackgroundColor(
                        ContextCompat
                            .getColor(binding.root.context, R.color.secondaryColor)
                    )
                titleText
                    .setTextColor(
                        ContextCompat
                            .getColor(binding.root.context, R.color.secondaryTextColor)
                    )
                contentText
                    .setTextColor(
                        ContextCompat
                            .getColor(binding.root.context, R.color.secondaryTextColor)
                    )
                timeStamp
                    .setTextColor(
                        ContextCompat
                            .getColor(binding.root.context, R.color.secondaryTextColor)
                    )
                shareButton.setColorFilter(
                    ContextCompat
                        .getColor(binding.root.context, R.color.secondaryTextColor)
                )
            }
        }

        private fun setAsRead() {
            binding.apply {
                notificationCard
                    .setCardBackgroundColor(
                        ContextCompat
                            .getColor(binding.root.context, R.color.primaryColor)
                    )
                titleText
                    .setTextColor(
                        ContextCompat
                            .getColor(binding.root.context, R.color.primaryTextColor)
                    )
                contentText
                    .setTextColor(
                        ContextCompat
                            .getColor(binding.root.context, R.color.primaryTextColor)
                    )
                timeStamp
                    .setTextColor(
                        ContextCompat
                            .getColor(binding.root.context, R.color.primaryTextColor)
                    )
                shareButton.setColorFilter(
                    ContextCompat
                        .getColor(binding.root.context, R.color.primaryTextColor)
                )
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