package com.dalmoa.android.core

import android.content.Context
import com.dalmoa.android.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://dalmoa.duckdns.org/"

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
