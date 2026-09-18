package com.dalmoa.android.feature.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dalmoa.android.databinding.NoticeFragmentDetailBinding

class NoticeDetailFragment : Fragment() {

    private var _binding: NoticeFragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NoticeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NoticeFragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.notice.observe(viewLifecycleOwner) { notice ->
            if (notice == null) return@observe
            if (notice.version.isNullOrEmpty()) {
                binding.tvVersion.visibility = View.GONE
            } else {
                binding.tvVersion.visibility = View.VISIBLE
                binding.tvVersion.text = "v${notice.version}"
            }
            binding.tvTitle.text = notice.title
            binding.tvDate.text = notice.createdAt.substringBefore("T")
            binding.tvContent.text = notice.content
        }

        val noticeId = requireArguments().getLong("noticeId")
        viewModel.loadNotice(noticeId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
