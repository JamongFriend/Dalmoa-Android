package com.dalmoa.android.feature.subscribe

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dalmoa.android.R
import com.dalmoa.android.core.ApiClient
import com.dalmoa.android.core.TokenManager
import com.dalmoa.android.core.formatDate
import com.dalmoa.android.data.remote.api.SubscribeApi
import com.dalmoa.android.databinding.SubscribeFragmentDetailBinding
import com.dalmoa.android.model.Subscribe
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class SubscribeDetailFragment : Fragment() {

    private var _binding: SubscribeFragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SubscribeViewModel by viewModels()
    private var subscribe: Subscribe? = null
    private lateinit var tokenManager: TokenManager

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

        tokenManager = TokenManager(requireContext())
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        subscribe?.let {
            binding.tvDetailName.text = it.name
            val fmt = DecimalFormat("#,###")
            val categoryLabel = if (it.category == com.dalmoa.android.model.SubCategory.ETC && !it.customCategoryTag.isNullOrEmpty()) {
                it.customCategoryTag
            } else {
                it.category.displayName
            }
            val priceText = if (it.currency == "USD") {
                "$${fmt.format(it.price)} (약 ${fmt.format(it.convertedPriceKrw)}원)"
            } else {
                "${fmt.format(it.price)}원"
            }
            binding.tvDetailPrice.text = "${priceText} · ${categoryLabel}"
            binding.tvDetailDate.text = "결제일: ${formatDate(it.date)}"
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
                deleteSubscribe()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun deleteSubscribe() {
        val subscribeId = subscribe?.id ?: return
        val memberId = tokenManager.getMemberId()

        lifecycleScope.launch {
            try {
                val api = ApiClient.retrofit.create(SubscribeApi::class.java)
                val response = api.deleteSubscribe(subscribeId, memberId)

                if (response.isSuccessful) {
                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "삭제 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
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
