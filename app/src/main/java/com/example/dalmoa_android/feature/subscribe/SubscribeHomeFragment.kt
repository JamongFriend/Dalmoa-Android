package com.example.dalmoa_android.feature.subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dalmoa_android.R
import com.example.dalmoa_android.adapter.SubscribeAdapter
import com.example.dalmoa_android.databinding.SubscribeFragmentHomeBinding
import com.example.dalmoa_android.model.SubCategory
import com.example.dalmoa_android.model.Subscribe
import java.text.DecimalFormat

class SubscribeHomeFragment : Fragment() {

    private var _binding: SubscribeFragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var subscribeAdapter: SubscribeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SubscribeFragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.subscribeAddFragment)
        }
        
        loadDummyData()
    }

    private fun setupRecyclerView() {
        subscribeAdapter = SubscribeAdapter(emptyList()) { item ->
            findNavController().navigate(R.id.subscribeDetailFragment)
        }
        binding.rvSubscribeList.apply {
            adapter = subscribeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun loadDummyData() {
        val dummyData = listOf(
            Subscribe(1, "넷플릭스", 17000.0, "KRW", "2024-04-14", SubCategory.OTT),
            Subscribe(2, "유튜브 프리미엄", 14900.0, "KRW", "2024-04-20", SubCategory.MUSIC),
            Subscribe(3, "쿠팡 와우", 4990.0, "KRW", "2024-04-25", SubCategory.LIFESTYLE)
        )
        
        if (dummyData.isEmpty()) {
            binding.rvSubscribeList.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvSubscribeList.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
            subscribeAdapter.updateData(dummyData)
            
            binding.tvSubscribeCount.text = "구독 중인 서비스: ${dummyData.size}개"
            val total = dummyData.sumOf { it.price }
            binding.tvTotalAmount.text = "${DecimalFormat("#,###").format(total)}원"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}