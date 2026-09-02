package com.dalmoa.android.feature.subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dalmoa.android.databinding.SubscribeFragmentListBinding
import com.dalmoa.android.model.Term
import java.text.DecimalFormat

class SubscribeListFragment : Fragment() {

    private var _binding: SubscribeFragmentListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SubscribeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SubscribeFragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadSubscriptions()
    }

    private fun observeViewModel() {
        viewModel.subscriptions.observe(viewLifecycleOwner) { subscriptions ->
            if (subscriptions.isNotEmpty()) {
                updateTermCards()
                updateTopTerm()
            }
        }
    }

    private fun updateTermCards() {
        val amounts = viewModel.getSpendingByTerm()
        val counts = viewModel.getCountByTerm()
        val decimalFormat = DecimalFormat("#,###")

        binding.tvWeekAmount.text = "${decimalFormat.format(amounts[Term.WEEK] ?: 0.0)}원"
        binding.tvWeekCount.text = "${counts[Term.WEEK] ?: 0}개 구독 중"

        binding.tvMonthAmount.text = "${decimalFormat.format(amounts[Term.MONTH] ?: 0.0)}원"
        binding.tvMonthCount.text = "${counts[Term.MONTH] ?: 0}개 구독 중"

        binding.tvYearAmount.text = "${decimalFormat.format(amounts[Term.YEAR] ?: 0.0)}원"
        binding.tvYearCount.text = "${counts[Term.YEAR] ?: 0}개 구독 중"
    }

    private fun updateTopTerm() {
        val topTermPair = viewModel.getSpendingByTerm().maxByOrNull { it.value }
        if (topTermPair != null && topTermPair.value > 0) {
            val formattedAmount = DecimalFormat("#,###").format(topTermPair.value)
            binding.tvTopTermSummary.text = "이번 달은 ${topTermPair.key.displayName} 결제에\n가장 많은 금액(${formattedAmount}원)을 사용하셨어요!"
        } else {
            binding.tvTopTermSummary.text = "등록된 구독 정보가 없습니다."
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
