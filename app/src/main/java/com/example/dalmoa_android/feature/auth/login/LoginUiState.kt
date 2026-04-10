package com.example.dalmoa_android.feature.auth.login

import com.example.dalmoa_android.data.remote.dto.LoginResponse

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val data: LoginResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
