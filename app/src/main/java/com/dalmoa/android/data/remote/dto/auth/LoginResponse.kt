package com.dalmoa.android.data.remote.dto.auth

data class LoginResponse(
    val memberId: Long,
    val accessToken: String? = null,
    val refreshToken: String? = null
)
