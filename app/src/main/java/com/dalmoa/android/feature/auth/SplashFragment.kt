package com.dalmoa.android.feature.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dalmoa.android.R
import com.dalmoa.android.core.ApiClient
import com.dalmoa.android.core.TokenManager
import com.dalmoa.android.databinding.AuthFragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: AuthFragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AuthFragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
    }

    private fun observeState() {
        ApiClient.init(requireContext())
        val tokenManager = TokenManager(requireContext())

        // 1.5초 정도 로고를 보여준 뒤 이동
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                if (tokenManager.getToken() != null) {
                    // 로그인 된 상태 -> 홈으로
                    findNavController().navigate(R.id.action_splash_to_dashboard)
                } else {
                    // 로그인 안 된 상태 -> 로그인 화면으로
                    findNavController().navigate(R.id.action_splash_to_login)
                }
            }
        }, 1500)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
