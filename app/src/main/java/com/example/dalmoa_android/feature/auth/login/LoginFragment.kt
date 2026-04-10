package com.example.dalmoa_android.feature.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.dalmoa_android.core.ApiClient
import com.example.dalmoa_android.core.TokenManager
import com.example.dalmoa_android.data.repository.AuthRepository
import com.example.dalmoa_android.R
import com.example.dalmoa_android.data.remote.api.AuthApi
import com.example.dalmoa_android.databinding.AuthFragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: AuthFragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: LoginViewModel
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AuthFragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 초기화 (dalmoa 패키지 기준)
        ApiClient.init(requireContext())
        tokenManager = TokenManager(requireContext())
        val authApi = ApiClient.retrofit.create(AuthApi::class.java)
        val repo = AuthRepository(authApi)
        vm = ViewModelProvider(this, LoginViewModelFactory(repo))[LoginViewModel::class.java]

        // 2. 로그인 버튼 이벤트
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            vm.login(email, password)
        }

        // 3. 회원가입 이동 (dalmoa 액션 ID: action_login_to_signup)
        binding.tvGoSignup.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signup)
        }

        // 4. 로그인 상태 관리 및 화면 이동
        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collect { state ->
                when (state) {
                    is LoginUiState.Loading -> {
                        Toast.makeText(requireContext(), "로그인 중..", Toast.LENGTH_SHORT).show()
                    }
                    is LoginUiState.Success -> {
                        state.data.accessToken?.let { tokenManager.saveToken(it) }
                        tokenManager.saveMemberId(state.data.memberId)

                        Toast.makeText(requireContext(), "환영합니다", Toast.LENGTH_SHORT).show()

                        findNavController().navigate(R.id.action_login_to_dashboard)
                    }
                    is LoginUiState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
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
