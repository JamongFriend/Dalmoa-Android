package com.dalmoa.android.data.remote.api

import com.dalmoa.android.data.remote.dto.auth.LoginRequest
import com.dalmoa.android.data.remote.dto.auth.LoginResponse
import com.dalmoa.android.data.remote.dto.auth.ReissueRequest
import com.dalmoa.android.data.remote.dto.auth.SignUpRequest
import com.dalmoa.android.data.remote.dto.auth.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/reissue")
    suspend fun reissue(@Body request: ReissueRequest): Response<LoginResponse>

    @POST("api/member/signUp")
    suspend fun signup(@Body request: SignUpRequest): Response<SignUpResponse>
}
