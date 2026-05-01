package com.dalmoa.android.feature.auth.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dalmoa.android.BuildConfig
import com.dalmoa.android.R
import com.dalmoa.android.core.TokenManager
import com.dalmoa.android.databinding.MemberFragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: MemberFragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = MemberFragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvVersion.text = "v${BuildConfig.VERSION_NAME}"

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_profileEdit)
        }

        binding.btnLogout.setOnClickListener {
            tokenManager.clear()
            Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }

        binding.btnWithdraw.setOnClickListener {
            Toast.makeText(requireContext(), "준비 중인 기능입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
