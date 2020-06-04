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
import com.example.goldameirl.viewmodel.BranchListener
import com.example.goldameirl.viewmodel.BranchesViewModel

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

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(BranchesViewModel::class.java)

        adapter = BranchAdapter(BranchListener(viewModel.phoneClick))

        binding.apply {
            viewModel = this@BranchesFragment.viewModel
            branchesList.adapter = adapter
        }

        viewModel.branches.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                binding.list = it
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}
