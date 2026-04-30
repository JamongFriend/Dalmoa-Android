package com.dalmoa.android.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmoa.android.data.remote.dto.auth.LoginRequest
import com.dalmoa.android.data.repository.AuthRepository
import com.dalmoa.android.data.remote.dto.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val state: StateFlow<LoginUiState> = _state

    fun login(email: String, password: String, rememberMe: Boolean) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = LoginUiState.Error("이메일/비밀번호를 입력하세요.")
            return
        }
        viewModelScope.launch {
            _state.value = LoginUiState.Loading
            try {
                val request = LoginRequest(email, password, rememberMe)
                val response = repository.login(request)

                if (response.isSuccessful && response.body() != null) {
                    _state.value = LoginUiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "로그인 실패"
                    }
                    _state.value = LoginUiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _state.value = LoginUiState.Error(e.message ?: "에러 발생")
            }
        }
    }
}
