package com.example.dalmoa_android.data.repository


import com.example.dalmoa_android.data.remote.dto.LoginRequest
import com.example.dalmoa_android.data.remote.dto.LoginResponse
import com.example.dalmoa_android.data.remote.api.AuthApi
import com.example.dalmoa_android.data.remote.dto.SignUpRequest
import com.example.dalmoa_android.data.remote.dto.SignUpResponse
import retrofit2.Response

class AuthRepository (
    private val authApi: AuthApi
) {
    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return authApi.login(request)
    }
    suspend fun signup(request: SignUpRequest): Response<SignUpResponse> {
        return authApi.signup(request)
    }
}
