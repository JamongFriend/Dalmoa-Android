package com.dalmoa.android.core

import android.content.Context
import com.dalmoa.android.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // debug 빌드는 로컬 백엔드(10.0.2.2), release 빌드는 EC2 운영 서버를 바라봄 (app/build.gradle buildTypes 참고)
    private val BASE_URL = BuildConfig.BASE_URL

    private var tokenManager: TokenManager? = null

    fun init(context: Context) {
        if (tokenManager == null) {
            tokenManager = TokenManager(context.applicationContext)
        }
    }

    private val okHttp: OkHttpClient by lazy {
        val manager = tokenManager ?: throw IllegalStateException("ApiClient must be initialized with init(context) before access.")
        val builder = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(manager))
            .authenticator(TokenAuthenticator(manager))
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
        builder.build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
