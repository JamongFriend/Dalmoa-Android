package com.dalmoa.android.data.remote.api

import com.dalmoa.android.data.remote.dto.subscribe.SubscribeRequest
import com.dalmoa.android.model.Subscribe
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SubscribeApi {
    @GET("api/subscribe/list/{memberId}")
    suspend fun getSubscriptions(@Path("memberId") memberId: Long): Response<List<Subscribe>>

    @POST("api/subscribe/{memberId}")
    suspend fun createSubscribe(
        @Path("memberId") memberId: Long,
        @Body request: SubscribeRequest
    ): Response<Subscribe>

    @PUT("api/subscribe/{subscribeId}")
    suspend fun editSubscribe(
        @Path("subscribeId") subscribeId: Long,
        @Body request: SubscribeRequest
    ): Response<Subscribe>

    @DELETE("api/subscribe/{subscribeId}/{memberId}")
    suspend fun deleteSubscribe(
        @Path("subscribeId") subscribeId: Long,
        @Path("memberId") memberId: Long
    ): Response<Void>
}
