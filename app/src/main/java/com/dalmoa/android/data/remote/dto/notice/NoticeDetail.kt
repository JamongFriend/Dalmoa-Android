package com.dalmoa.android.data.remote.dto.notice

data class NoticeDetail(
    val id: Long,
    val version: String?,
    val title: String,
    val content: String,
    val createdAt: String
)
