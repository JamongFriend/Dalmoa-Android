package com.dalmoa.android.data.remote.dto.auth

data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean
)
