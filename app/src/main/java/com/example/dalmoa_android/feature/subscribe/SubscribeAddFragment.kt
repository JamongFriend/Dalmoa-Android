package com.example.dalmoa_android.feature.subscribe

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dalmoa_android.databinding.SubscribeFragmentAddBinding
import java.util.*

class SubscribeAddFragment : Fragment() {

    private var _binding: SubscribeFragmentAddBinding? = null
    private val binding get() = _binding!!

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
            binding.etDate.setText("${selectedYear}-${selectedMonth + 1}-${selectedDay}")
        }, year, month, day).show()
    }

    private fun saveSubscribe() {
        val name = binding.etName.text.toString()
        val price = binding.etPrice.text.toString()
        val date = binding.etDate.text.toString()

        if (name.isEmpty() || price.isEmpty() || date.isEmpty()) {
            Toast.makeText(context, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(context, "${name} 구독이 추가되었습니다.", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}