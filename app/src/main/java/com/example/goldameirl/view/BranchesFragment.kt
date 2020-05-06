package com.example.goldameirl.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.goldameirl.R
import com.example.goldameirl.databinding.FragmentBranchesBinding
import com.example.goldameirl.viewmodel.BranchAdapter
import com.example.goldameirl.viewmodel.BranchesViewModel
import com.example.goldameirl.viewmodel.BranchesViewModelFactory


class BranchesFragment : Fragment(){

    private lateinit var viewModel: BranchesViewModel
    private lateinit var adapter: BranchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application

        val binding = DataBindingUtil.inflate<FragmentBranchesBinding>(
            inflater,
            R.layout.fragment_branches, container, false
        )

        val viewModelFactory = BranchesViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(BranchesViewModel::class.java)
        binding.viewModel = viewModel
        adapter = BranchAdapter()
        binding.branchesList.adapter = adapter

        viewModel.branches.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                if(it.isEmpty()) {
                    binding.branchesList.visibility = View.GONE
                    binding.branchesEmpty.visibility = View.VISIBLE
                }
                else {
                    adapter.submitList(it)
                    binding.branchesEmpty.visibility = View.GONE
                    binding.branchesList.visibility = View.VISIBLE
                }
            }
        })

        binding.branchesList.adapter

        binding.abcChip.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.onChipChecked(buttonView, isChecked)
        }

        binding.locationChip.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.onChipChecked(buttonView, isChecked)
        }

        return binding.root
    }
}
