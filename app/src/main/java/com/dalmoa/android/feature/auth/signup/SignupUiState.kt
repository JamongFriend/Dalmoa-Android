package com.dalmoa.android.feature.auth.signup

import com.dalmoa.android.data.remote.dto.auth.SignUpResponse

sealed class SignupUiState {
    object Idle : SignupUiState()
    object Loading : SignupUiState()
    data class Success(val data: SignUpResponse) : SignupUiState()
    data class Error(val message: String) : SignupUiState()
}
