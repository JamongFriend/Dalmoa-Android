package com.dalmoa.android.data.repository

import com.dalmoa.android.data.remote.api.MemberApi
import com.dalmoa.android.data.remote.dto.ProfileUpdateRequest
import com.dalmoa.android.data.remote.dto.member.MemberResponse
import retrofit2.Response

class MemberRepository(private val memberApi: MemberApi) {
    suspend fun getMember(memberId: Long): Response<MemberResponse> {
        return memberApi.getMember(memberId)
    }

    suspend fun updateMember(memberId: Long, request: ProfileUpdateRequest): Response<MemberResponse> {
        return memberApi.updateMember(memberId, request)
    }
}
