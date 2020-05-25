package com.example.goldameirl.viewmodel

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.goldameirl.R
import com.example.goldameirl.databinding.AlertListItemBinding
import com.example.goldameirl.model.Alert
import com.example.goldameirl.model.AlertManager

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

        fun bind(item: Alert) {
            binding.apply {
                alert = item

                shareButton.setOnClickListener {
                    shareClick(this, item)
                }

                deleteButton.setOnClickListener {
                    AlertManager.getInstance(root.context)?.delete(item)
                }

                checkBox.apply {
                    isChecked = item.isRead

                    setOnClickListener {
                        item.isRead = isChecked
                        AlertManager.getInstance(context)?.update(item)
                    }
                }

                executePendingBindings()
            }
        }

        private fun shareClick(binding: AlertListItemBinding, item: Alert) {
            binding.apply {
                val shareIntent = Intent()

                shareIntent.apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        root.context.getString(R.string.share_message) +
                                "${item.content} at ${item.title}!"
                    )
                    type = "text/plain"
                }

                root.context.startActivity(shareIntent)
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