package com.dalmoa.android.core

fun formatDate(dateStr: String): String {
    return try {
        val parts = dateStr.substringBefore("T").split("-")
        "${parts[0]}년 ${parts[1].toInt()}월 ${parts[2].toInt()}일"
    } catch (e: Exception) {
        dateStr.substringBefore("T")
    }
}
