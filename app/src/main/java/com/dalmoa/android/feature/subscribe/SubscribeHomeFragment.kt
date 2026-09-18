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

        binding.cardSummary.setOnClickListener {
            MonthlySpendingDialogFragment().show(childFragmentManager, "MonthlySpendingDialog")
        }
    }

    // 구독 추가 후 돌아왔을 때 데이터 갱신을 위해 onResume 사용
    override fun onResume() {
        super.onResume()
        viewModel.loadSubscriptions()
        viewModel.loadDashboard()
    }

    private fun setupRecyclerView() {
        subscribeAdapter = SubscribeAdapter(emptyList()) { item ->
            val bundle = Bundle().apply {
                putParcelable("subscribe", item)
            }
            findNavController().navigate(R.id.subscribeDetailFragment, bundle)
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

        viewModel.dashboard.observe(viewLifecycleOwner) { dashboard ->
            updateCompareLastMonth(dashboard)
        }
    }

    private fun updateCompareLastMonth(dashboard: com.dalmoa.android.model.SubscribeDashboard?) {
        if (dashboard == null) {
            binding.tvCompareLastMonth.text = "지난달 대비 -"
            binding.tvCompareLastMonth.setTextColor(android.graphics.Color.parseColor("#666666"))
            return
        }

        val decimalFormat = DecimalFormat("#,###")
        val diffAmount = dashboard.diffAmount
        val sign = if (diffAmount > 0) "+" else if (diffAmount < 0) "-" else ""
        val amountText = decimalFormat.format(kotlin.math.abs(diffAmount))
        val percentText = decimalFormat.format(kotlin.math.abs(dashboard.diffPercent))

        binding.tvCompareLastMonth.text = if (diffAmount == 0.0) {
            "지난달과 동일"
        } else {
            "지난달 대비 ${sign}${amountText}원 (${sign}${percentText}%)"
        }
        binding.tvCompareLastMonth.setTextColor(
            when {
                diffAmount > 0 -> android.graphics.Color.parseColor("#E53935")
                diffAmount < 0 -> android.graphics.Color.parseColor("#1E88E5")
                else -> android.graphics.Color.parseColor("#666666")
            }
        )
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
