package com.dalmoa.android.data.remote.dto.subscribe

import com.dalmoa.android.model.SubCategory
import com.dalmoa.android.model.Term

data class SubscribeRequest(
    val name: String,
    val price: Double,
    val currency: String = "KRW",
    val date: String,
    val subCategory: SubCategory,
    val customCategoryTag: String? = null,
    val term: Term = Term.MONTH
)
