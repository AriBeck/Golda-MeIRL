package com.example.goldameirl.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.goldameirl.R
import com.example.goldameirl.databinding.FragmentNotificationsBinding
import com.example.goldameirl.viewmodel.NotificationsViewModel.NotificationAdapter
import com.example.goldameirl.viewmodel.NotificationsViewModel
import com.example.goldameirl.viewmodel.NotificationsViewModelFactory

class NotificationsFragment : Fragment() {
    private lateinit var viewModel: NotificationsViewModel
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val binding = DataBindingUtil.inflate<FragmentNotificationsBinding>(
            inflater,
            R.layout.fragment_notifications, container, false
        )
        val viewModelFactory = NotificationsViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(NotificationsViewModel::class.java)
        binding.viewModel = viewModel
        adapter = NotificationAdapter()
        binding.notificationList.adapter = adapter

        viewModel.notifications?.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                if(it.isEmpty()) {
                    binding.notificationList.visibility = View.GONE
                    binding.notificationsEmpty.visibility = View.VISIBLE
                }
                else {
                    adapter.submitList(it)
                    binding.notificationsEmpty.visibility = View.GONE
                    binding.notificationList.visibility = View.VISIBLE
                }
            }
        })
        return binding.root
    }

}
