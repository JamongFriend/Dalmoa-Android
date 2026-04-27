package com.dalmoa.android.data.remote.api

import com.dalmoa.android.data.remote.dto.subscribe.SubscribeRequest
import com.dalmoa.android.model.Subscribe
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SubscribeApi {
    @GET("api/subscribe/list/{memberId}")
    suspend fun getSubscriptions(@Path("memberId") memberId: Long): Response<List<Subscribe>>

    @POST("api/subscribe/{memberId}")
    suspend fun createSubscribe(
        @Path("memberId") memberId: Long,
        @Body request: SubscribeRequest
    ): Response<Subscribe>
}
