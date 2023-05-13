package com.zelspeno.fitnesskit_testapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.zelspeno.fitnesskit_testapp.databinding.FragmentHomeBinding
import com.zelspeno.fitnesskit_testapp.ui.Repositories
import com.zelspeno.fitnesskit_testapp.utils.viewModelCreator
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private var adapter: CustomScheduleRecyclerAdapter? = null
    private var childAdapter: CustomScheduleChildRecyclerAdapter? = null

    private val viewModel by viewModelCreator {
        HomeViewModel(
            Repositories.scheduleRepository,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getWorkDayList()
                fillUI()
            }
        }

        return binding.root
    }

    private fun fillUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.workDay.collect {
                    adapter = CustomScheduleRecyclerAdapter(it)
                    sendDataToRecyclerView(adapter!!)
                }
            }
        }
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
        adapter = null
        childAdapter = null
    }
}