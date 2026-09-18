package com.dalmoa.android.data.remote.api

import com.dalmoa.android.data.remote.dto.notice.NoticeDetail
import com.dalmoa.android.data.remote.dto.notice.NoticeListItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface NoticeApi {
    @GET("api/notices")
    suspend fun getNotices(): Response<List<NoticeListItem>>

    @GET("api/notices/{id}")
    suspend fun getNotice(@Path("id") id: Long): Response<NoticeDetail>
}
