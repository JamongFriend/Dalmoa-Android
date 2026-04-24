package com.example.dalmoa_android.feature

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.dalmoa_android.R
import com.example.dalmoa_android.core.ApiClient
import com.example.dalmoa_android.core.TokenManager
import com.example.dalmoa_android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 초기화 및 자동 로그인 체크
        ApiClient.init(this)
        val tokenManager = TokenManager(this)
        if (!tokenManager.isRememberMe()) {
            tokenManager.clear()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 네비게이션
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 하단 네비게이션 바와 네비게이션 컨트롤러 연결
        binding.bottomNavigation.setupWithNavController(navController)

        // 화면별 하단 바 가시성 제어 (리스너 추가)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_stats, R.id.navigation_profile -> {
                    // 메인 탭 화면에서는 하단 바를 보여줌
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                else -> {
                    // 그 외 화면(예: 상세, 등록 등)에서는 하단 바를 숨김
                    binding.bottomNavigation.visibility = View.GONE
                }
            }
        }
    }
}