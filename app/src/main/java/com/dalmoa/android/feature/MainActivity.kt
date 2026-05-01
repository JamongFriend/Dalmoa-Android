package com.dalmoa.android.feature

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dalmoa.android.R
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import com.dalmoa.android.core.ApiClient
import com.dalmoa.android.core.TokenManager
import com.dalmoa.android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 초기화
        ApiClient.init(this)
        val tokenManager = TokenManager(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 네비게이션
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination(
            if (tokenManager.getToken() != null) R.id.navigation_home else R.id.loginFragment
        )
        navController.setGraph(graph, null)

        // 하단 네비게이션 바와 네비게이션 컨트롤러 연결
        binding.bottomNavigation.setupWithNavController(navController)

        // 뒤로가기 핸들러 설정
        setupBackPressedHandler()

        // 화면별 하단 바 가시성 제어 (리스너 추가)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_list, R.id.navigation_stats, R.id.navigation_profile -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
            }
        }
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentDestination = navController.currentDestination?.id

                when (currentDestination) {
                    R.id.navigation_home -> {
                        // 홈 화면인 경우: 더블 뒤로가기 종료 로직
                        if (System.currentTimeMillis() - backPressedTime < 2000) {
                            finish()
                        } else {
                            backPressedTime = System.currentTimeMillis()
                            Toast.makeText(this@MainActivity, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    R.id.navigation_list, R.id.navigation_stats, R.id.navigation_profile -> {
                        // 목록, 통계나 프로필인 경우: 홈 화면으로 이동
                        binding.bottomNavigation.selectedItemId = R.id.navigation_home
                    }
                    else -> {
                        // 그 외 화면: 이전 화면으로 이동
                        if (!navController.popBackStack()) {
                            // 더 이상 돌아갈 화면이 없으면 홈으로 (안전장치)
                            binding.bottomNavigation.selectedItemId = R.id.navigation_home
                        }
                    }
                }
            }
        })
    }
}
