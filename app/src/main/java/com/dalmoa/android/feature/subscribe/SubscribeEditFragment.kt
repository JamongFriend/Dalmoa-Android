package com.dalmoa.android.feature.subscribe

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dalmoa.android.R
import com.dalmoa.android.databinding.SubscribeFragmentEditBinding
import com.dalmoa.android.model.SubCategory
import com.dalmoa.android.model.Subscribe
import com.dalmoa.android.core.ApiClient
import com.dalmoa.android.data.remote.api.SubscribeApi
import com.dalmoa.android.data.remote.dto.subscribe.SubscribeRequest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SubscribeEditFragment : Fragment() {

    private var _binding: SubscribeFragmentEditBinding? = null
    private val binding get() = _binding!!
    private var subscribe: Subscribe? = null
    private var selectedDay: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SubscribeFragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("subscribe", Subscribe::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("subscribe")
        }

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        subscribe?.let {
            binding.etEditServiceName.setText(it.name)
            binding.etEditPrice.setText(it.price.toString())

            selectedDay = it.date.substringBefore("T").split("-").getOrNull(2)?.toIntOrNull() ?: 1
            binding.etEditPaymentDate.setText("${selectedDay}일")

            if (it.currency == "USD") {
                binding.toggleEditCurrency.check(R.id.btn_edit_currency_usd)
            } else {
                binding.toggleEditCurrency.check(R.id.btn_edit_currency_krw)
            }

            val chipId = when (it.category) {
                SubCategory.OTT -> R.id.chipEditOtt
                SubCategory.MUSIC -> R.id.chipEditMusic
                SubCategory.GAME -> R.id.chipEditGame
                SubCategory.LIFESTYLE -> R.id.chipEditLifestyle
                SubCategory.FINANCE -> R.id.chipEditFinance
                SubCategory.ETC -> R.id.chipEditEtc
            }
            binding.chipGroupEditCategory.check(chipId)

            if (it.category == SubCategory.ETC) {
                binding.tilEditCustomCategory.visibility = View.VISIBLE
                binding.etEditCustomCategory.setText(it.customCategoryTag ?: "")
            }
        }
    }

    private fun setupListeners() {
        binding.btnEditBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.etEditPaymentDate.setOnClickListener {
            showDayPicker()
        }

        binding.chipGroupEditCategory.setOnCheckedStateChangeListener { _, checkedIds ->
            val isEtc = checkedIds.contains(R.id.chipEditEtc)
            binding.tilEditCustomCategory.visibility = if (isEtc) View.VISIBLE else View.GONE
            if (!isEtc) binding.etEditCustomCategory.setText("")
        }

        binding.btnUpdateSubscribe.setOnClickListener {
            updateSubscribe()
        }
    }

    private fun showDayPicker() {
        val picker = NumberPicker(requireContext()).apply {
            minValue = 1
            maxValue = 31
            value = selectedDay
            displayedValues = (1..31).map { "${it}일" }.toTypedArray()
        }
        AlertDialog.Builder(requireContext())
            .setTitle("매월 결제일 선택")
            .setView(picker)
            .setPositiveButton("확인") { _, _ ->
                selectedDay = picker.value
                binding.etEditPaymentDate.setText("${selectedDay}일")
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun updateSubscribe() {
        val originalId = subscribe?.id ?: return
        val name = binding.etEditServiceName.text.toString().trim()
        val priceStr = binding.etEditPrice.text.toString().trim()
        val date = String.format("2000-01-%02d", selectedDay)
        val currency = if (binding.toggleEditCurrency.checkedButtonId == R.id.btn_edit_currency_usd) "USD" else "KRW"

        val subCategory = when (binding.chipGroupEditCategory.checkedChipId) {
            R.id.chipEditOtt -> SubCategory.OTT
            R.id.chipEditMusic -> SubCategory.MUSIC
            R.id.chipEditGame -> SubCategory.GAME
            R.id.chipEditLifestyle -> SubCategory.LIFESTYLE
            R.id.chipEditFinance -> SubCategory.FINANCE
            R.id.chipEditEtc -> SubCategory.ETC
            else -> null
        }

        val customCategoryTag = if (subCategory == SubCategory.ETC) {
            binding.etEditCustomCategory.text.toString().trim()
        } else null

        if (name.isEmpty() || priceStr.isEmpty() || subCategory == null) {
            Toast.makeText(context, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (subCategory == SubCategory.ETC && customCategoryTag.isNullOrEmpty()) {
            Toast.makeText(context, "카테고리 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull() ?: 0.0

        val request = SubscribeRequest(
            name = name,
            price = price,
            currency = currency,
            date = date,
            subCategory = subCategory,
            customCategoryTag = customCategoryTag
        )

        lifecycleScope.launch {
            try {
                val api = ApiClient.retrofit.create(SubscribeApi::class.java)
                val response = api.editSubscribe(originalId, request)

                if (response.isSuccessful) {
                    Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "수정 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
