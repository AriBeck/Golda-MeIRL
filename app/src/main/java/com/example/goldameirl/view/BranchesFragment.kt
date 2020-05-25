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
import com.example.goldameirl.model.Branch
import com.example.goldameirl.viewmodel.BranchAdapter
import com.example.goldameirl.viewmodel.BranchesViewModel
import com.example.goldameirl.viewmodel.BranchesViewModelFactory

class BranchesFragment : Fragment(){
    private lateinit var viewModel: BranchesViewModel
    private lateinit var adapter: BranchAdapter
    private lateinit var binding: FragmentBranchesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireActivity().application

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_branches, container, false
        )

        val viewModelFactory = BranchesViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(BranchesViewModel::class.java)
        adapter = BranchAdapter()
        binding.apply {
            viewModel = viewModel
            branchesList.adapter = adapter
        }

        viewModel.branches.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                setListVisibility(it)
            }
        })

        return binding.root
    }

    private fun setListVisibility(list: List<Branch>) {
        if (list.isEmpty()) {
            binding.apply {
                binding.branchesList.visibility = View.GONE
                binding.branchesEmpty.visibility = View.VISIBLE
            }
        } else {
            adapter.submitList(list)

            binding.apply {
                branchesEmpty.visibility = View.GONE
                branchesList.visibility = View.VISIBLE
            }
        }
    }
}
