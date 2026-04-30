package com.dalmoa.android.feature.subscribe

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dalmoa.android.R
import com.dalmoa.android.core.ApiClient
import com.dalmoa.android.core.TokenManager
import com.dalmoa.android.data.remote.api.SubscribeApi
import com.dalmoa.android.data.remote.dto.subscribe.SubscribeRequest
import com.dalmoa.android.databinding.SubscribeFragmentAddBinding
import com.dalmoa.android.model.SubCategory
import kotlinx.coroutines.launch
import java.util.*

class SubscribeAddFragment : Fragment() {

    private var _binding: SubscribeFragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager

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

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            saveSubscribe()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            // 백엔드 LocalDate 형식(yyyy-MM-dd)에 맞게 포맷팅
            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.etDate.setText(formattedDate)
        }, year, month, day).show()
    }

    private fun saveSubscribe() {
        val name = binding.etName.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        
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

        if (name.isEmpty() || priceStr.isEmpty() || date.isEmpty() || subCategory == null) {
            Toast.makeText(context, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull() ?: 0.0
        val memberId = tokenManager.getMemberId()

        if (memberId == -1L) {
            Toast.makeText(context, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = SubscribeRequest(
            name = name,
            price = price,
            date = date,
            subCategory = subCategory
        )

        lifecycleScope.launch {
            try {
                val api = ApiClient.retrofit.create(SubscribeApi::class.java)
                val response = api.createSubscribe(memberId, request)
                
                if (response.isSuccessful) {
                    Toast.makeText(context, "${name} 구독이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "저장 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
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
