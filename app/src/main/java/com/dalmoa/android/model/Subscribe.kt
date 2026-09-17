package com.dalmoa.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Subscribe(
    val id: Long? = null,
    val name: String,
    // 운영 서버(main/docker)는 아직 "category" 키를 쓰고, develop 백엔드는 "subCategory"로 바뀐 상태라
    // 배포 상태와 무관하게 둘 다 받아들이도록 alternate 지정
    @SerializedName(value = "subCategory", alternate = ["category"])
    val category: SubCategory,
    val customCategoryTag: String? = null,
    val date: String,
    val price: Double,
    val currency: String,
    val term: Term = Term.MONTH,
    val convertedPriceKrw: Double = 0.0,
    // 이번 달(조회 기준월) 기준 주/월/연 환산 원화 월 지출액
    val monthlyKrwAmount: Double = 0.0,
    val registeredAt: String? = null
) : Parcelable
