package com.example.frontend.ui.service

import LoginRequest
import LoginResponse
import RegisterRequest
import RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response


interface ApiService {
    @POST("register")
    suspend fun register(@Body body: RegisterRequest): Response<RegisterResponse>

    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

}