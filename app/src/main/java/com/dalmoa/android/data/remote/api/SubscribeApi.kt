package com.dalmoa.android.data.remote.api

import com.dalmoa.android.data.remote.dto.subscribe.SubscribeRequest
import com.dalmoa.android.model.Subscribe
import com.dalmoa.android.model.SubscribeDashboard
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SubscribeApi {
    // year, month를 둘 다 넘기면 해당 달 기준, 아니면 서버가 이번 달 기준으로 조회
    @GET("api/subscribe/list")
    suspend fun getSubscriptions(
        @Query("year") year: Int?,
        @Query("month") month: Int?
    ): Response<List<Subscribe>>

    @GET("api/subscribe/dashboard")
    suspend fun getDashboard(
        @Query("year") year: Int?,
        @Query("month") month: Int?
    ): Response<SubscribeDashboard>

    @POST("api/subscribe")
    suspend fun createSubscribe(
        @Body request: SubscribeRequest
    ): Response<Subscribe>

    @PUT("api/subscribe/{subscribeId}")
    suspend fun editSubscribe(
        @Path("subscribeId") subscribeId: Long,
        @Body request: SubscribeRequest
    ): Response<Subscribe>

    @DELETE("api/subscribe/{subscribeId}")
    suspend fun deleteSubscribe(
        @Path("subscribeId") subscribeId: Long
    ): Response<Void>
}
