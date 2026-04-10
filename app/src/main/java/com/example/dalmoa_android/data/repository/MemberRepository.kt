package com.example.dalmoa_android.data.repository

import com.example.dalmoa_android.data.remote.api.MemberApi
import com.example.dalmoa_android.data.remote.dto.ProfileUpdateRequest
import com.example.dalmoa_android.data.remote.dto.member.MemberResponse
import retrofit2.Response

class MemberRepository(private val memberApi: MemberApi) {
    suspend fun getMember(memberId: Long): Response<MemberResponse> {
        return memberApi.getMember(memberId)
    }

    suspend fun updateMember(memberId: Long, request: ProfileUpdateRequest): Response<MemberResponse> {
        return memberApi.updateMember(memberId, request)
    }
}
