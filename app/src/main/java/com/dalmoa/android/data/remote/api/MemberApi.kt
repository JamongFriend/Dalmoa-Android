package com.dalmoa.android.data.remote.api

import com.dalmoa.android.data.remote.dto.ProfileUpdateRequest
import com.dalmoa.android.data.remote.dto.member.MemberResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface MemberApi {
    @GET("api/member/{memberId}")
    suspend fun getMember(
        @Path("memberId") memberId: Long
    ): Response<MemberResponse>

    @PUT("api/member/{memberId}")
    suspend fun updateMember(
        @Path("memberId") memberId: Long,
        @Body request: ProfileUpdateRequest
    ): Response<MemberResponse>
}
