package com.example.goldameirl.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.goldameirl.R
import com.example.goldameirl.databinding.FragmentNotificationsBinding
import com.example.goldameirl.viewmodel.NotificationsViewModel

/**
 * A simple [Fragment] subclass.
 */
class NotificationsFragment : Fragment() {

    private lateinit var viewModel: NotificationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentNotificationsBinding>(inflater,
            R.layout.fragment_notifications,  container, false)
        viewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        binding.viewModel = viewModel

        return binding.root
    }

}
