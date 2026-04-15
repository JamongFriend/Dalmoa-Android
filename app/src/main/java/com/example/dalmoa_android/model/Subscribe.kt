package com.example.dalmoa_android.model

data class Subscribe(
    val id: Long? = null,
    val name: String,
    val category: SubCategory,
    val date: String,
    val price: Double,
    val currency: String
)
