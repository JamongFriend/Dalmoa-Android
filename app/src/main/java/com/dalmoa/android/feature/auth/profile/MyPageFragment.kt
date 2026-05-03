package com.dalmoa.android.feature.auth.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dalmoa.android.R
import com.dalmoa.android.core.ApiClient
import com.dalmoa.android.core.TokenManager
import com.dalmoa.android.data.remote.api.MemberApi
import com.dalmoa.android.data.repository.MemberRepository
import com.dalmoa.android.databinding.MemberFragmentProfileBinding
import kotlinx.coroutines.launch

class MyPageFragment : Fragment() {
    private var _binding: MemberFragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager
    private lateinit var viewModel: MyPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = MemberFragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupListeners()
        observeState()

        // JWT에서 이메일 즉시 표시 (API 응답 전 fallback)
        tokenManager.getEmailFromToken()?.let { binding.tvEmail.text = it }

        // 회원정보 불러오기
        val myId = tokenManager.getMemberId()
        if (myId != -1L) {
            viewModel.getMember(myId)
        }
    }

    private fun setupViewModel() {
        val api = ApiClient.retrofit.create(MemberApi::class.java)
        val repository = MemberRepository(api)
        val factory = MyPageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MyPageViewModel::class.java]
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_myPage_to_profileEdit)
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_myPage_to_settings)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is MyPageUiState.Loading -> {
                        binding.pbProfileLoading.visibility = View.VISIBLE
                    }
                    is MyPageUiState.Success -> {
                        binding.pbProfileLoading.visibility = View.GONE
                        binding.tvNickname.text = state.member.name
                        binding.tvEmail.text = state.member.email
                    }
                    is MyPageUiState.Error -> {
                        binding.pbProfileLoading.visibility = View.GONE
                        if (tokenManager.getToken() == null) {
                            Toast.makeText(requireContext(), "세션이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
                            val navOptions = androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.nav_graph, true)
                                .build()
                            findNavController().navigate(R.id.loginFragment, null, navOptions)
                        } else {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

