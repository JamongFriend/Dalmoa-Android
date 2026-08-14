package com.dalmoa.android.feature.subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dalmoa.android.R
import com.dalmoa.android.core.ApiClient
import com.dalmoa.android.core.TokenManager
import com.dalmoa.android.core.WEEKDAY_NAMES
import com.dalmoa.android.core.encodeMonthDate
import com.dalmoa.android.core.encodeWeekDate
import com.dalmoa.android.core.encodeYearDate
import com.dalmoa.android.data.remote.api.SubscribeApi
import com.dalmoa.android.data.remote.dto.subscribe.SubscribeRequest
import com.dalmoa.android.databinding.SubscribeFragmentAddBinding
import com.dalmoa.android.model.SubCategory
import com.dalmoa.android.model.Term
import com.dalmoa.android.data.remote.dto.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.*

class SubscribeAddFragment : Fragment() {

    private var _binding: SubscribeFragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager
    private var selectedTerm: Term = Term.MONTH
    private var selectedDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    private var selectedWeekday: Int = run {
        val dow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        if (dow == Calendar.SUNDAY) 7 else dow - 1
    }
    private var selectedYearMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var selectedYearDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SubscribeFragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        ApiClient.init(requireContext())
        tokenManager = TokenManager(requireContext())

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        updateDateField()
        binding.etDate.setOnClickListener {
            when (selectedTerm) {
                Term.WEEK -> showWeekdayPicker()
                Term.YEAR -> showYearPicker()
                Term.MONTH -> showDayPicker()
            }
        }

        binding.toggleCurrency.check(R.id.btnKrw)

        binding.chipGroupTerm.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedTerm = when (checkedIds.firstOrNull()) {
                R.id.chipTermWeek -> Term.WEEK
                R.id.chipTermYear -> Term.YEAR
                else -> Term.MONTH
            }
            updateDateField()
        }

        binding.chipGroupCategory.setOnCheckedStateChangeListener { _, checkedIds ->
            val isEtc = checkedIds.contains(R.id.chipAddEtc)
            binding.tilCustomCategory.visibility = if (isEtc) View.VISIBLE else View.GONE
            if (!isEtc) binding.etCustomCategory.setText("")
        }

        binding.btnSave.setOnClickListener {
            saveSubscribe()
        }
    }

    private fun updateDateField() {
        binding.tilDate.hint = when (selectedTerm) {
            Term.WEEK -> "결제 요일"
            Term.YEAR -> "결제 월/일"
            Term.MONTH -> "매월 결제일"
        }
        binding.etDate.setText(
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

    private fun saveSubscribe() {
        val name = binding.etName.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val date = when (selectedTerm) {
            Term.WEEK -> encodeWeekDate(selectedWeekday)
            Term.YEAR -> encodeYearDate(selectedYearMonth, selectedYearDay)
            Term.MONTH -> encodeMonthDate(selectedDay)
        }

        // 선택된 카테고리 가져오기
        val subCategory = when (binding.chipGroupCategory.checkedChipId) {
            R.id.chipAddOtt -> SubCategory.OTT
            R.id.chipAddMusic -> SubCategory.MUSIC
            R.id.chipAddGame -> SubCategory.GAME
            R.id.chipAddLifestyle -> SubCategory.LIFESTYLE
            R.id.chipAddFinance -> SubCategory.FINANCE
            R.id.chipAddEtc -> SubCategory.ETC
            else -> null
        }

        val customCategoryTag = if (subCategory == SubCategory.ETC) {
            binding.etCustomCategory.text.toString().trim()
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
        val currency = if (binding.toggleCurrency.checkedButtonId == R.id.btnUsd) "USD" else "KRW"
        val memberId = tokenManager.getMemberId()
        val token = tokenManager.getToken()

        if (memberId == -1L || token.isNullOrBlank()) {
            Toast.makeText(context, "로그인 세션이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

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
                val response = api.createSubscribe(request)
                
                if (response.isSuccessful) {
                    Toast.makeText(context, "${name} 구독이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    if (response.code() == 403) {
                        Toast.makeText(context, "접근 권한이 없습니다 (403). 다시 로그인해 보세요.", Toast.LENGTH_LONG).show()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "저장 실패: ${response.code()}"
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
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
