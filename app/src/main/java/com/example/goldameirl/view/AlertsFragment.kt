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
import com.example.goldameirl.viewmodel.AlertsViewModel
import com.example.goldameirl.viewmodel.AlertsViewModelFactory

class AlertsFragment : Fragment() {
    private lateinit var viewModel: AlertsViewModel
    private lateinit var adapter: AlertAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireActivity().application

        val binding = DataBindingUtil.inflate<FragmentAlertsBinding>(
            inflater,
            R.layout.fragment_alerts, container, false
        )

        val viewModelFactory = AlertsViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(AlertsViewModel::class.java)

        binding.viewModel = viewModel
        adapter = AlertAdapter()
        binding.alertList.adapter = adapter

        viewModel.alerts?.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                if(it.isEmpty()) {
                    binding.alertList.visibility = View.GONE
                    binding.alertsEmpty.visibility = View.VISIBLE
                }
                else {
                    adapter.submitList(it)
                    binding.alertsEmpty.visibility = View.GONE
                    binding.alertList.visibility = View.VISIBLE
                }
            }
        })
        return binding.root
    }
}
