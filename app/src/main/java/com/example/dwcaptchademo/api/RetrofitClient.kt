package com.example.dwcaptchademo.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.dwcaptchademo.BuildConfig

object RetrofitClient {
    private const val TAG = "RetrofitClient"
    private const val DEV_URL = "http://10.0.2.2:3001"
    private const val PRODUCTION_URL = "https://simple-login-backend.onrender.com"
    
    private val BASE_URL = if (BuildConfig.DEBUG) DEV_URL else PRODUCTION_URL

    init {
        Log.d(TAG, "Using backend URL: $BASE_URL (Debug mode: ${BuildConfig.DEBUG})")
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
} 