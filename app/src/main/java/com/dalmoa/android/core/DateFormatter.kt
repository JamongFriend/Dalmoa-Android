package com.dalmoa.android.core

fun formatDate(dateStr: String): String {
    return try {
        val day = dateStr.substringBefore("T").split("-")[2].toInt()
        "${day}일"
    } catch (e: Exception) {
        dateStr.substringBefore("T")
    }
}
