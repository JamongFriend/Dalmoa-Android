package com.dalmoa.android.model

data class SubscribeDashboard(
    val totalAmount: Double = 0.0,
    val previousTotalAmount: Double = 0.0,
    val diffAmount: Double = 0.0,
    val diffPercent: Double = 0.0,
    val categorySums: Map<SubCategory, Double> = emptyMap()
)
