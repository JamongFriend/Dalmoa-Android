package com.dalmoa.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SubCategory(val displayName: String) : Parcelable {
    OTT("OTT"),
    MUSIC("음악"),
    GAME("게임"),
    LIFESTYLE("생활"),
    FINANCE("금융"),
    ETC("기타")
}
