package com.dalmoa.android.feature.subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dalmoa.android.R
import com.dalmoa.android.adapter.SubscribeAdapter
import com.dalmoa.android.core.TokenManager
import com.dalmoa.android.databinding.SubscribeFragmentListBinding

class SubscribeListFragment : Fragment() {

    private var _binding: SubscribeFragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var subscribeAdapter: SubscribeAdapter
    private val viewModel: SubscribeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SubscribeFragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        
        binding.fabAddSubscribe.setOnClickListener {
            findNavController().navigate(R.id.subscribeAddFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val tokenManager = TokenManager(requireContext())
        val memberId = tokenManager.getMemberId()
        if (memberId != -1L) {
            viewModel.loadSubscriptions(memberId)
        } else {
            viewModel.loadSubscriptions(1L)
        }
    }

    private fun setupRecyclerView() {
        subscribeAdapter = SubscribeAdapter(emptyList()) { item ->
            val bundle = Bundle().apply {
                putParcelable("subscribe", item)
            }
            findNavController().navigate(R.id.subscribeDetailFragment, bundle)
        }
        binding.rvSubscribeList.apply {
            adapter = subscribeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        viewModel.subscriptions.observe(viewLifecycleOwner) { subscriptions ->
            subscribeAdapter.updateData(subscriptions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
