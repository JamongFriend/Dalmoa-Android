package com.example.dalmoa_android.feature.subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dalmoa_android.R
import com.example.dalmoa_android.adapter.SubscribeAdapter
import com.example.dalmoa_android.databinding.SubscribeFragmentHomeBinding
import com.example.dalmoa_android.feature.subscribe.SubscribeViewModel
import com.example.dalmoa_android.model.SubCategory
import com.example.dalmoa_android.model.Subscribe
import java.text.DecimalFormat

class SubscribeHomeFragment : Fragment() {

    private var _binding: SubscribeFragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var subscribeAdapter: SubscribeAdapter
    private val viewModel: SubscribeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SubscribeFragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupCategoryFilter()
        observeViewModel()
        
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.subscribeAddFragment)
        }
    }

    private fun setupRecyclerView() {
        subscribeAdapter = SubscribeAdapter(emptyList()) { item ->
            findNavController().navigate(R.id.subscribeDetailFragment)
        }
        binding.rvSubscribeList.apply {
            adapter = subscribeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupCategoryFilter() {
        binding.chipGroupCategory.setOnCheckedStateChangeListener { group, checkedIds ->
            val category = when (checkedIds.firstOrNull()) {
                R.id.chipOtt -> SubCategory.OTT
                R.id.chipMusic -> SubCategory.MUSIC
                R.id.chipGame -> SubCategory.GAME
                R.id.chipLifestyle -> SubCategory.LIFESTYLE
                R.id.chipFinance -> SubCategory.FINANCE
                R.id.chipEtc -> SubCategory.ETC
                else -> null // chipAll or none
            }
            viewModel.filterByCategory(category)
        }
    }

    private fun observeViewModel() {
        viewModel.filteredSubscriptions.observe(viewLifecycleOwner) { subscriptions ->
            updateUI(subscriptions)
        }
    }

    private fun updateUI(subscriptions: List<Subscribe>) {
        if (subscriptions.isEmpty()) {
            binding.rvSubscribeList.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvSubscribeList.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
            subscribeAdapter.updateData(subscriptions)
        }
        
        binding.tvSubscribeCount.text = "구독 중인 서비스: ${viewModel.getSubscriptionCount()}개"
        val total = viewModel.getTotalAmount()
        binding.tvTotalAmount.text = "${DecimalFormat("#,###").format(total)}원"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}