package com.dalmoa.android.core

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("dalmoa_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val MEMBER_ID = "member_id"
        private const val REMEMBER_ME = "remember_me"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(ACCESS_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(ACCESS_TOKEN, null)
    }

    fun saveRefreshToken(token: String) {
        prefs.edit().putString(REFRESH_TOKEN, token).apply()
    }

    fun getRefreshToken(): String? {
        return prefs.getString(REFRESH_TOKEN, null)
    }

    fun saveMemberId(id: Long) {
        prefs.edit().putLong(MEMBER_ID, id).apply()
    }

    fun getMemberId(): Long {
        return prefs.getLong(MEMBER_ID, -1L)
    }

    fun saveRememberMe(enabled: Boolean) {
        prefs.edit().putBoolean(REMEMBER_ME, enabled).apply()
    }

    fun isRememberMe(): Boolean {
        return prefs.getBoolean(REMEMBER_ME, false)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
