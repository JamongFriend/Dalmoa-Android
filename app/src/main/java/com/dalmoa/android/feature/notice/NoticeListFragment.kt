package com.dalmoa.android.feature.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dalmoa.android.R
import com.dalmoa.android.adapter.NoticeAdapter
import com.dalmoa.android.databinding.NoticeFragmentListBinding

class NoticeListFragment : Fragment() {

    private var _binding: NoticeFragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var noticeAdapter: NoticeAdapter
    private val viewModel: NoticeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NoticeFragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setupRecyclerView()
        observeViewModel()
        viewModel.loadNotices()
    }

    private fun setupRecyclerView() {
        noticeAdapter = NoticeAdapter(emptyList()) { item ->
            val bundle = Bundle().apply {
                putLong("noticeId", item.id)
            }
            findNavController().navigate(R.id.noticeDetailFragment, bundle)
        }
        binding.rvNoticeList.apply {
            adapter = noticeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        viewModel.notices.observe(viewLifecycleOwner) { notices ->
            noticeAdapter.updateData(notices)
            binding.tvEmpty.visibility = if (notices.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
