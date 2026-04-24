package com.example.dalmoa_android.data.repository


import com.example.dalmoa_android.data.remote.dto.auth.LoginRequest
import com.example.dalmoa_android.data.remote.dto.auth.LoginResponse
import com.example.dalmoa_android.data.remote.api.AuthApi
import com.example.dalmoa_android.data.remote.dto.auth.ReissueRequest
import com.example.dalmoa_android.data.remote.dto.auth.SignUpRequest
import com.example.dalmoa_android.data.remote.dto.auth.SignUpResponse
import retrofit2.Response

class AuthRepository (
    private val authApi: AuthApi
) {
    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return authApi.login(request)
    }

    suspend fun reissue(request: ReissueRequest): Response<LoginResponse> {
        return authApi.reissue(request)
    }

    suspend fun signup(request: SignUpRequest): Response<SignUpResponse> {
        return authApi.signup(request)
    }
}
