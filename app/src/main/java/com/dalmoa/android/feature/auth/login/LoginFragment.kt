package com.dalmoa.android.feature.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dalmoa.android.core.ApiClient
import com.dalmoa.android.core.TokenManager
import com.dalmoa.android.data.repository.AuthRepository
import com.dalmoa.android.R
import com.dalmoa.android.data.remote.api.AuthApi
import com.dalmoa.android.databinding.AuthFragmentLoginBinding
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

        // 1. 초기화
        ApiClient.init(requireContext())
        tokenManager = TokenManager(requireContext())
        val authApi = ApiClient.retrofit.create(AuthApi::class.java)
        val repo = AuthRepository(authApi)
        vm = ViewModelProvider(this, LoginViewModelFactory(repo))[LoginViewModel::class.java]

        // 2. 로그인 버튼 이벤트 (XML ID: btn_login -> ViewBinding: btnLogin)
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val rememberMe = binding.cbRememberMe.isChecked
            vm.login(email, password, rememberMe)
        }

        // 3. 회원가입 버튼 이벤트 (XML ID: tv_go_signup -> ViewBinding: tvGoSignup)
        binding.tvGoSignup.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signup)
        }

        binding.tvFindIdPw.setOnClickListener {
            Toast.makeText(requireContext(), "비밀번호 찾기 기능은 준비중입니다.", Toast.LENGTH_SHORT).show()
        }

        // 자동 로그인 체크 상태 복원
        binding.cbRememberMe.isChecked = tokenManager.isRememberMe()

        // 4. 로그인 상태 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collect { state ->
                when (state) {
                    is LoginUiState.Loading -> {
                        Toast.makeText(requireContext(), "로그인 중...", Toast.LENGTH_SHORT).show()
                    }
                    is LoginUiState.Success -> {
                        state.data.accessToken?.let { tokenManager.saveToken(it) }
                        state.data.refreshToken?.let { tokenManager.saveRefreshToken(it) }
                        tokenManager.saveMemberId(state.data.memberId)
                        tokenManager.saveRememberMe(binding.cbRememberMe.isChecked)

                        Toast.makeText(requireContext(), "환영합니다!", Toast.LENGTH_SHORT).show()

                        // 홈 화면으로 이동
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
