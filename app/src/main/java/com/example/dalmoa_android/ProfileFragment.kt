package com.example.dalmoa_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dalmoa_android.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 프로필 수정 버튼 클릭
        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(context, "프로필 수정 화면으로 이동합니다.", Toast.LENGTH_SHORT).show()
        }

        // 환경 설정 버튼 클릭
        binding.btnSettings.setOnClickListener {
            Toast.makeText(context, "환경 설정 화면으로 이동합니다.", Toast.LENGTH_SHORT).show()
        }

        // 로그아웃 버튼 클릭
        binding.btnLogout.setOnClickListener {
            Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}