package com.dalmoa.android.data.repository

import com.dalmoa.android.data.remote.api.MemberApi
import com.dalmoa.android.data.remote.dto.ProfileUpdateRequest
import com.dalmoa.android.data.remote.dto.member.MemberResponse
import retrofit2.Response

class MemberRepository(private val memberApi: MemberApi) {
    suspend fun getMember(): Response<MemberResponse> {
        return memberApi.getMember()
    }

    suspend fun updateMember(request: ProfileUpdateRequest): Response<MemberResponse> {
        return memberApi.updateMember(request)
    }
}
