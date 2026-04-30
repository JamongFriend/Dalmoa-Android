package com.dalmoa.android.feature.subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dalmoa.android.R
import com.dalmoa.android.adapter.SubscribeAdapter
import com.dalmoa.android.core.ApiClient
import com.dalmoa.android.core.TokenManager
import com.dalmoa.android.databinding.SubscribeFragmentHomeBinding
import com.dalmoa.android.model.SubCategory
import com.dalmoa.android.model.Subscribe
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

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
        setupDateNavigation()
        observeViewModel()
        
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.subscribeAddFragment)
        }
    }

    // 구독 추가 후 돌아왔을 때 데이터 갱신을 위해 onResume 사용
    override fun onResume() {
        super.onResume()
        val tokenManager = TokenManager(requireContext())
        val memberId = tokenManager.getMemberId()
        if (memberId != -1L) {
            viewModel.loadSubscriptions(memberId)
        } else {
            viewModel.loadSubscriptions(1L) // 기본값 유지
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

    private fun setupDateNavigation() {
        binding.btnPrevMonth.setOnClickListener {
            viewModel.prevMonth()
        }
        binding.btnNextMonth.setOnClickListener {
            viewModel.nextMonth()
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
                else -> null
            }
            viewModel.filterByCategory(category)
        }
    }

    private fun observeViewModel() {
        viewModel.filteredSubscriptions.observe(viewLifecycleOwner) { subscriptions ->
            updateUI(subscriptions)
        }
        
        // 날짜 변경 시 텍스트 업데이트
        viewModel.currentCalendar.observe(viewLifecycleOwner) { calendar ->
            val sdf = SimpleDateFormat("yyyy년 M월", Locale.KOREA)
            binding.tvCurrentMonth.text = sdf.format(calendar.time)
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
