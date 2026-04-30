package com.dalmoa.android.feature.subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dalmoa.android.databinding.SubscribeFragmentDetailBinding

import android.os.Build
import com.dalmoa.android.R
import com.dalmoa.android.model.Subscribe
import java.text.DecimalFormat

class SubscribeDetailFragment : Fragment() {

    private var _binding: SubscribeFragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SubscribeViewModel by viewModels()
    private var subscribe: Subscribe? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SubscribeFragmentDetailBinding.inflate(inflater, container, false)
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
            binding.tvDetailName.text = it.name
            val formattedPrice = DecimalFormat("#,###").format(it.price)
            binding.tvDetailPrice.text = "${formattedPrice}원 (${it.category.displayName})"
            binding.tvDetailDate.text = "결제일: ${it.date}"
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnEditSubscribe.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("subscribe", subscribe)
            }
            findNavController().navigate(R.id.action_detail_to_edit, bundle)
        }

        binding.btnDeleteSubscribe.setOnClickListener {
            showDeleteConfirmDialog()
        }
    }

    private fun showDeleteConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("구독 삭제")
            .setMessage("이 구독 정보를 정말 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                // 삭제 로직 호출 (ViewModel 연동)
                Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
