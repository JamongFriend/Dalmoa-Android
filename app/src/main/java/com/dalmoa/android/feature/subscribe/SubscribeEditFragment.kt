package com.dalmoa.android.feature.subscribe

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dalmoa.android.R
import com.dalmoa.android.databinding.SubscribeFragmentEditBinding
import com.dalmoa.android.model.SubCategory
import com.dalmoa.android.model.Subscribe
import com.dalmoa.android.model.Term
import com.dalmoa.android.core.ApiClient
import com.dalmoa.android.core.WEEKDAY_NAMES
import com.dalmoa.android.core.decodeMonthDay
import com.dalmoa.android.core.decodeWeekday
import com.dalmoa.android.core.decodeYearDay
import com.dalmoa.android.core.decodeYearMonth
import com.dalmoa.android.core.encodeMonthDate
import com.dalmoa.android.core.encodeWeekDate
import com.dalmoa.android.core.encodeYearDate
import com.dalmoa.android.data.remote.api.SubscribeApi
import com.dalmoa.android.data.remote.dto.subscribe.SubscribeRequest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SubscribeEditFragment : Fragment() {

    private var _binding: SubscribeFragmentEditBinding? = null
    private val binding get() = _binding!!
    private var subscribe: Subscribe? = null
    private var selectedTerm: Term = Term.MONTH
    private var selectedDay: Int = 1
    private var selectedWeekday: Int = 1
    private var selectedYearMonth: Int = 1
    private var selectedYearDay: Int = 1

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

            selectedTerm = it.term
            when (it.term) {
                Term.WEEK -> selectedWeekday = decodeWeekday(it.date)
                Term.YEAR -> {
                    selectedYearMonth = decodeYearMonth(it.date)
                    selectedYearDay = decodeYearDay(it.date)
                }
                Term.MONTH -> selectedDay = decodeMonthDay(it.date)
            }
            updateDateField()

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

            val termChipId = when (it.term) {
                Term.WEEK -> R.id.chipEditTermWeek
                Term.MONTH -> R.id.chipEditTermMonth
                Term.YEAR -> R.id.chipEditTermYear
            }
            binding.chipGroupEditTerm.check(termChipId)
        }
    }

    private fun setupListeners() {
        binding.btnEditBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.etEditPaymentDate.setOnClickListener {
            when (selectedTerm) {
                Term.WEEK -> showWeekdayPicker()
                Term.YEAR -> showYearPicker()
                Term.MONTH -> showDayPicker()
            }
        }

        binding.chipGroupEditTerm.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedTerm = when (checkedIds.firstOrNull()) {
                R.id.chipEditTermWeek -> Term.WEEK
                R.id.chipEditTermYear -> Term.YEAR
                else -> Term.MONTH
            }
            updateDateField()
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

    private fun updateDateField() {
        binding.tilEditPaymentDate.hint = when (selectedTerm) {
            Term.WEEK -> "결제 요일 수정"
            Term.YEAR -> "결제 월/일 수정"
            Term.MONTH -> "매월 결제일 수정"
        }
        binding.etEditPaymentDate.setText(
            when (selectedTerm) {
                Term.WEEK -> "매주 ${WEEKDAY_NAMES[selectedWeekday - 1]}"
                Term.YEAR -> "매년 ${selectedYearMonth}월 ${selectedYearDay}일"
                Term.MONTH -> "${selectedDay}일"
            }
        )
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
                updateDateField()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showWeekdayPicker() {
        val picker = NumberPicker(requireContext()).apply {
            minValue = 1
            maxValue = 7
            value = selectedWeekday
            displayedValues = WEEKDAY_NAMES.toTypedArray()
        }
        AlertDialog.Builder(requireContext())
            .setTitle("결제 요일 선택")
            .setView(picker)
            .setPositiveButton("확인") { _, _ ->
                selectedWeekday = picker.value
                updateDateField()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showYearPicker() {
        val monthPicker = NumberPicker(requireContext()).apply {
            minValue = 1
            maxValue = 12
            value = selectedYearMonth
            displayedValues = (1..12).map { "${it}월" }.toTypedArray()
        }
        val dayPicker = NumberPicker(requireContext()).apply {
            minValue = 1
            maxValue = 31
            value = selectedYearDay
            displayedValues = (1..31).map { "${it}일" }.toTypedArray()
        }
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(monthPicker, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            addView(dayPicker, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        }
        AlertDialog.Builder(requireContext())
            .setTitle("결제 월/일 선택")
            .setView(container)
            .setPositiveButton("확인") { _, _ ->
                selectedYearMonth = monthPicker.value
                selectedYearDay = dayPicker.value
                updateDateField()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun updateSubscribe() {
        val originalId = subscribe?.id ?: return
        val name = binding.etEditServiceName.text.toString().trim()
        val priceStr = binding.etEditPrice.text.toString().trim()
        val date = when (selectedTerm) {
            Term.WEEK -> encodeWeekDate(selectedWeekday)
            Term.YEAR -> encodeYearDate(selectedYearMonth, selectedYearDay)
            Term.MONTH -> encodeMonthDate(selectedDay)
        }
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

        val term = selectedTerm

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
            customCategoryTag = customCategoryTag,
            term = term
        )

        lifecycleScope.launch {
            try {
                val api = ApiClient.retrofit.create(SubscribeApi::class.java)
                val response = api.editSubscribe(originalId, request)

                if (response.isSuccessful) {
                    Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack(R.id.navigation_home, false)
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
