package com.dalmoa.android.data.remote.dto.auth

data class SignUpRequest(
    val email: String,
    val name: String,
    val password: String,
    val confirmPassword: String,
    val birthDate: String
)
