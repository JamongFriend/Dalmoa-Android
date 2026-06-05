package com.dalmoa.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Subscribe(
    val id: Long? = null,
    val name: String,
    val category: SubCategory,
    val customCategoryTag: String? = null,
    val date: String,
    val price: Double,
    val currency: String,
    val convertedPriceKrw: Double = 0.0
) : Parcelable
