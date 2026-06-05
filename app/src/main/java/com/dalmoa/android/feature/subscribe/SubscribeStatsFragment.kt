package com.dalmoa.android.feature.subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dalmoa.android.core.TokenManager
import com.dalmoa.android.databinding.SubscribeFragmentStatsBinding
import java.text.DecimalFormat

class SubscribeStatsFragment : Fragment() {

    private var _binding: SubscribeFragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SubscribeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SubscribeFragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        val memberId = TokenManager(requireContext()).getMemberId()
        if (memberId != -1L) {
            viewModel.loadSubscriptions(memberId)
        }
    }

    private fun observeViewModel() {
        viewModel.subscriptions.observe(viewLifecycleOwner) { subscriptions ->
            if (subscriptions.isNotEmpty()) {
                updateChart()
                updateTopCategory()
            }
        }
    }

    private fun updateChart() {
        val spendingData = viewModel.getSpendingByCategory()
        binding.pieChartView.setData(spendingData)
    }

    private fun updateTopCategory() {
        val topCategoryPair = viewModel.getTopSpendingCategory()
        if (topCategoryPair != null) {
            val (category, amount) = topCategoryPair
            val formattedAmount = DecimalFormat("#,###").format(amount)
            binding.tvTopCategorySummary.text = "이번 달은 ${category.name} 서비스에\n가장 많은 금액(${formattedAmount}원)을 사용하셨어요!"
        } else {
            binding.tvTopCategorySummary.text = "등록된 구독 정보가 없습니다."
        }
    }

    override fun onDestroyView() {        super.onDestroyView()
        _binding = null
    }
}
