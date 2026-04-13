package com.example.dalmoa_android.model

import java.time.LocalDateTime

data class Subscribe(
    val id: Long? = null,
    val name: String,
    val price: Double,
    val currency: String = "KRW",
    val date: String,
    val subCategory: SubCategory,
    val customCategoryTag: String? = null
)