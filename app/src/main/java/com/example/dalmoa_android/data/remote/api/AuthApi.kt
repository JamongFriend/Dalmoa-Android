package com.example.dalmoa_android.data.remote.api

import com.example.dalmoa_android.data.remote.dto.LoginRequest
import com.example.dalmoa_android.data.remote.dto.LoginResponse
import com.example.dalmoa_android.data.remote.dto.SignUpRequest
import com.example.dalmoa_android.data.remote.dto.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/member/signUp")
    suspend fun signup(@Body request: SignUpRequest): Response<SignUpResponse>
}
