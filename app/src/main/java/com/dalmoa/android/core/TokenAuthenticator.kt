package com.dalmoa.android.core

import com.dalmoa.android.data.remote.api.AuthApi
import com.dalmoa.android.data.remote.dto.auth.ReissueRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenAuthenticator(private val tokenManager: TokenManager) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = tokenManager.getRefreshToken() ?: return null

        synchronized(this) {
            // Check if the token was already refreshed by another thread
            val currentToken = tokenManager.getToken()
            val requestToken = response.request.header("Authorization")?.replace("Bearer ", "")
            
            if (currentToken != requestToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            // Need to refresh
            val authApi = createAuthApi()
            val reissueResponse = runBlocking {
                try {
                    authApi.reissue(ReissueRequest(refreshToken))
                } catch (e: Exception) {
                    null
                }
            }

            if (reissueResponse != null && reissueResponse.isSuccessful) {
                val body = reissueResponse.body()
                if (body != null) {
                    body.accessToken?.let { tokenManager.saveToken(it) }
                    body.refreshToken?.let { tokenManager.saveRefreshToken(it) }
                    
                    return response.request.newBuilder()
                        .header("Authorization", "Bearer ${body.accessToken}")
                        .build()
                }
            }
            
            // Refresh failed or no body
            tokenManager.clear()
            return null
        }
    }

    private fun createAuthApi(): AuthApi {
        // Use a separate retrofit instance without AuthInterceptor/Authenticator to avoid recursion
        return Retrofit.Builder()
            .baseUrl("https://dalmoa.duckdns.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}
