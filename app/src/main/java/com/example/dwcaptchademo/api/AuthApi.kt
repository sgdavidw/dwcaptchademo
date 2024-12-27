package com.example.dwcaptchademo.api

import retrofit2.Response
import retrofit2.http.*

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class LoginResponse(
    val access_token: String
)

data class UserProfile(
    val id: Int,
    val email: String,
    val name: String
)

interface AuthApi {
    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<UserProfile>
} 