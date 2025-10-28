package com.example.frontend.ui.service

import LoginRequest
import LoginResponse
import RecipeCoversResponse
import RegisterRequest
import RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>


    @GET("recipe/covers")
    suspend fun getRecipeCovers(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 12,
        @Query("type") type: String? = null,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): Response<RecipeCoversResponse>
}