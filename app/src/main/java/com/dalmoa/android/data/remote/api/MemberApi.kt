package com.dalmoa.android.data.remote.api

import com.dalmoa.android.data.remote.dto.ProfileUpdateRequest
import com.dalmoa.android.data.remote.dto.member.MemberResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface MemberApi {
    @GET("api/member")
    suspend fun getMember(): Response<MemberResponse>

    @PUT("api/member")
    suspend fun updateMember(
        @Body request: ProfileUpdateRequest
    ): Response<MemberResponse>
}
