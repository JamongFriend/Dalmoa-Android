package com.dalmoa.android.feature.auth.login

import com.dalmoa.android.data.remote.dto.auth.LoginResponse

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val data: LoginResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
