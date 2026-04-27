package com.dalmoa.android.data.remote.api

import com.dalmoa.android.model.Subscribe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SubscribeApi {
    @GET("api/subscribe/list/{memberId}")
    suspend fun getSubscriptions(@Path("memberId") memberId: Long): Response<List<Subscribe>>
}
