package com.dalmoa.android.feature.subscribe

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.util.Calendar
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

        // Argument로부터 데이터 받기
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
            binding.etEditPaymentDate.setText(it.date.split("T")[0]) // 날짜 부분만 추출 (yyyy-MM-dd)
            
            // 통화 설정 (기본 KRW)
            if (it.currency == "USD") {
                binding.toggleEditCurrency.check(R.id.btn_edit_currency_usd)
            } else {
                binding.toggleEditCurrency.check(R.id.btn_edit_currency_krw)
            }

            // 카테고리 드롭다운 설정
            val categories = SubCategory.values().map { cat -> cat.displayName }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
            binding.spinnerEditCategory.setAdapter(adapter)
            binding.spinnerEditCategory.setText(it.category.displayName, false)
        }
    }

    private fun setupListeners() {
        binding.btnEditBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.etEditPaymentDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnUpdateSubscribe.setOnClickListener {
            updateSubscribe()
        }
    }

    private fun showDatePicker() {
        val currentDate = binding.etEditPaymentDate.text.toString()
        val calendar = Calendar.getInstance()

        if (currentDate.isNotEmpty()) {
            val parts = currentDate.split("-")
            if (parts.size == 3) {
                calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            }
        }

        DatePickerDialog(requireContext(), R.style.DatePickerTheme, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.etEditPaymentDate.setText(formattedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateSubscribe() {
        val originalId = subscribe?.id ?: return
        val name = binding.etEditServiceName.text.toString().trim()
        val priceStr = binding.etEditPrice.text.toString().trim()
        val date = binding.etEditPaymentDate.text.toString().trim()
        
        val currency = if (binding.toggleEditCurrency.checkedButtonId == R.id.btn_edit_currency_usd) "USD" else "KRW"
        
        // 선택된 카테고리displayName으로 매칭
        val selectedCategoryName = binding.spinnerEditCategory.text.toString()
        val category = SubCategory.values().find { it.displayName == selectedCategoryName } ?: SubCategory.ETC

        if (name.isEmpty() || priceStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(context, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull() ?: 0.0

        val request = SubscribeRequest(
            name = name,
            price = price,
            currency = currency,
            date = date,
            subCategory = category
        )

        lifecycleScope.launch {
            try {
                val api = ApiClient.retrofit.create(SubscribeApi::class.java)
                val response = api.editSubscribe(originalId, request)
                
                if (response.isSuccessful) {
                    Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                    // 상세보기 화면을 건너뛰고 목록이나 홈으로 돌아가기 위해 popBackStack 2번 또는 특정 목적지로 이동
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
