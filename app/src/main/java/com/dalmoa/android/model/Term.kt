package com.dalmoa.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Term(val displayName: String) : Parcelable {
    WEEK("주간"),
    MONTH("월간"),
    YEAR("연간")
}
