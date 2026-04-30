package com.dalmoa.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Subscribe(
    val id: Long? = null,
    val name: String,
    val category: SubCategory,
    val date: String,
    val price: Double,
    val currency: String
) : Parcelable
