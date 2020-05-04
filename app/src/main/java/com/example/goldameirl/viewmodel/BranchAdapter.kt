package com.example.goldameirl.viewmodel

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.goldameirl.databinding.BranchesListItemBinding
import com.example.goldameirl.model.Branch

class BranchAdapter:
    ListAdapter<Branch, BranchAdapter.ViewHolder>(BranchDiffCallBack()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: BranchesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Branch) {
            binding.branch = item
            binding.phoneText.setOnClickListener {
                val phoneIntent = Intent()
                phoneIntent.apply {
                    action = Intent.ACTION_DIAL
                    data = Uri.parse("tel:${item.phone}")
                }
                if (phoneIntent.resolveActivity(binding.root.context.packageManager) != null){
                    binding.root.context.startActivity(phoneIntent)
                }
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BranchesListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class BranchDiffCallBack : DiffUtil.ItemCallback<Branch>() {
    override fun areItemsTheSame(oldItem: Branch, newItem: Branch): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Branch, newItem: Branch): Boolean {
        return oldItem.id == newItem.id
    }
}