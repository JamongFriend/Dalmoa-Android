package com.example.dalmoa_android.feature.auth.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dalmoa_android.R
import com.example.dalmoa_android.core.TokenManager
import com.example.dalmoa_android.databinding.MemberFragmentProfileBinding

class MemberProfileFragment : Fragment() {

    private var _binding: MemberFragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager

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

        // 임시로 저장된 정보 표시 (실제로는 ViewModel을 통해 가져와야 함)
        binding.tvEmail.text = "user@example.com"
        binding.tvNickname.text = "회원님"

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_edit)
        }

        binding.btnSettings.setOnClickListener {
            Toast.makeText(context, "환경 설정 화면으로 이동합니다.", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            tokenManager.clear()
            Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}