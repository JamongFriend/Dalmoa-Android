package com.example.dalmoa_android.feature.auth.signup

import com.example.dalmoa_android.data.remote.dto.SignUpResponse

sealed class SignupUiState {
    object Idle : SignupUiState()
    object Loading : SignupUiState()
    data class Success(val data: SignUpResponse) : SignupUiState()
    data class Error(val message: String) : SignupUiState()
}
