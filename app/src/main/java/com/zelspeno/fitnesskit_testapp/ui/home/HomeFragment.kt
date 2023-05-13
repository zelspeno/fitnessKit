package com.zelspeno.fitnesskit_testapp.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.zelspeno.fitnesskit_testapp.databinding.FragmentHomeBinding
import com.zelspeno.fitnesskit_testapp.ui.Repositories
import com.zelspeno.fitnesskit_testapp.utils.viewModelCreator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private var adapter: CustomScheduleRecyclerAdapter? = null

    private val viewModel by viewModelCreator {
        HomeViewModel(
            Repositories.scheduleRepository,
        )
    }
    private var dataRV: MutableList<WorkDay>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = CustomScheduleRecyclerAdapter(dataRV)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                fillUIWithShimmer()
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    fillUIWithShimmer()
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        return binding.root
    }

    private suspend fun fillUIWithShimmer() {
        binding.homeScheduleRecyclerView.visibility = View.GONE
        binding.shimmerScheduleRecyclerView.visibility = View.VISIBLE
        binding.shimmerScheduleRecyclerView.startShimmer()
        delay(500)
        fillUI()
        binding.shimmerScheduleRecyclerView.stopShimmer()
        binding.homeScheduleRecyclerView.visibility = View.VISIBLE
        binding.shimmerScheduleRecyclerView.visibility = View.GONE
    }

    private fun fillUI() {
        if (isInternetConnected(requireContext())) {
            binding.noEConnectionTV.visibility = View.GONE
            binding.homeScheduleRecyclerView.visibility = View.VISIBLE
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    viewModel.getWorkDayList()
                    fillData()
                }
            }
        } else {
            binding.noEConnectionTV.visibility = View.VISIBLE
            binding.homeScheduleRecyclerView.visibility = View.GONE
        }
    }

    private fun fillData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.workDay.collect {
                    dataRV = it.toMutableList()
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

    private fun isInternetConnected(context: Context): Boolean {
        val conManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (conManager != null) {
            val capabilities = conManager.getNetworkCapabilities(conManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true
                else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true
                else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return true
            }
        }
        return false
    }

}