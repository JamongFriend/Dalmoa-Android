package com.dalmoa.android.feature.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dalmoa.android.data.remote.dto.auth.SignUpRequest
import com.dalmoa.android.data.repository.AuthRepository
import com.dalmoa.android.data.remote.dto.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow<SignupUiState>(SignupUiState.Idle)
    val state: StateFlow<SignupUiState> = _state

    fun signup(request: SignUpRequest) {
        viewModelScope.launch {
            _state.value = SignupUiState.Loading
            try {
                val response = repository.signup(request)
                if (response.isSuccessful && response.body() != null) {
                    _state.value = SignupUiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        errorResponse.message
                    } catch (e: Exception) {
                        "회원가입 실패: ${response.message()}"
                    }
                    _state.value = SignupUiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _state.value = SignupUiState.Error(e.message ?: "알 수 없는 오류 발생")
            }
        }
    }
}
