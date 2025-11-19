package com.example.frontend.ui.service

import GoogleLoginRequest
import LoginRequest
import LoginResponse
import RecipeCoversResponse
import RecipeResponse
import RegisterRequest
import RegisterResponse
import Review
import ReviewResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class MessageResponse(
    val message: String
)

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("auth/google")
    suspend fun loginGoogle(@Body body: GoogleLoginRequest): Response<LoginResponse>


    @GET("recipe/covers")
    suspend fun getRecipeCovers(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 12,
        @Query("type") type: String? = null,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): Response<RecipeCoversResponse>

    @GET("recipe/{id}")
    suspend fun getRecipe(
        @Path("id") recipeId: Int
    ): Response<RecipeResponse>

    @POST("recipe/create/recipeReviews")
    suspend fun createRecipeReviews(@Body review: Review
    ): Response<MessageResponse>

    @GET("recipe/reviews")
    suspend fun getRecipeReview(
        @Query("recipeId") recipeId: Int,
        @Query("userId") userId: Int
    ): Response<ReviewResponse>



    @GET("cart")
    suspend fun getCart() : Response<Cart>

    data class AddToCartRequest(
        val recipeId: Int
    )

    @POST("cart/add")
    suspend fun addToCart(@Body recipeId: AddToCartRequest) : Response<MessageResponse>


    @DELETE("cart/remove/{recipeId}")
    suspend fun deleteFromCart(
        @Path("recipeId") recipeId: Int
    ): Response<MessageResponse>

}