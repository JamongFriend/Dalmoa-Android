package com.dalmoa.android.feature.subscribe

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dalmoa.android.adapter.MonthlySpendingAdapter
import com.dalmoa.android.core.ApiClient
import com.dalmoa.android.databinding.DialogMonthlySpendingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MonthlySpendingDialogFragment : DialogFragment() {

    private var _binding: DialogMonthlySpendingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SubscribeViewModel by viewModels()
    private val adapter = MonthlySpendingAdapter(emptyList())

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        ApiClient.init(requireContext())
        _binding = DialogMonthlySpendingBinding.inflate(LayoutInflater.from(requireContext()))

        binding.rvMonthlySpending.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMonthlySpending.adapter = adapter
        binding.rvMonthlySpending.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        binding.btnClose.setOnClickListener { dismiss() }

        viewModel.monthlySpendings.observe(this) { list ->
            adapter.updateData(list)
        }
        viewModel.isMonthlyLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.rvMonthlySpending.visibility = if (loading) View.GONE else View.VISIBLE
        }

        viewModel.loadMonthlySpendings()

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
