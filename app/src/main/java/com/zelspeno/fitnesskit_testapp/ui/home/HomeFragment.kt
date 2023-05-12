package com.zelspeno.fitnesskit_testapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zelspeno.fitnesskit_testapp.databinding.FragmentHomeBinding
import com.zelspeno.fitnesskit_testapp.ui.Repositories
import com.zelspeno.fitnesskit_testapp.utils.viewModelCreator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private var adapter: CustomScheduleRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val viewModel by viewModelCreator {
            HomeViewModel(
            Repositories.scheduleRepository,
        )
    }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getWorkDayList()
                viewModel.workDay.collect {
                    adapter = CustomScheduleRecyclerAdapter(it)
                    sendDataToRecyclerView(adapter!!)
                }
            }
        }

        return binding.root
    }

    private fun sendDataToRecyclerView(adapter: CustomScheduleRecyclerAdapter) {
        val recyclerView = binding.homeScheduleRecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null
    }
}