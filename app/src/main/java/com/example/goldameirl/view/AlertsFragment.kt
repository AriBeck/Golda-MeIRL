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
import com.example.goldameirl.databinding.FragmentAlertsBinding
import com.example.goldameirl.viewmodel.AlertAdapter
import com.example.goldameirl.viewmodel.AlertListener
import com.example.goldameirl.viewmodel.AlertsViewModel

class AlertsFragment : Fragment() {
    private lateinit var viewModel: AlertsViewModel
    private lateinit var adapter: AlertAdapter
    private lateinit var binding: FragmentAlertsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireActivity().application

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_alerts, container, false
        )

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(AlertsViewModel::class.java)

        adapter = AlertAdapter(AlertListener(viewModel.shareClick,
            viewModel.deleteClick, viewModel.checkboxClick))

        binding.apply {
            binding.viewModel = viewModel
            binding.alertList.adapter = adapter
        }

        viewModel.alerts?.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                binding.list = it
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}
