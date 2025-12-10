package com.example.frontend.ui.service

import FacebookLoginRequest
import GoogleLoginRequest
import Comment
import LoginRequest
import LoginResponse
import RecipeCoversResponse
import RecipeResponse
import RegisterRequest
import RegisterResponse
import Review
import User
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
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

    @POST("auth/facebook")
    suspend fun loginFacebook(@Body request: FacebookLoginRequest): Response<LoginResponse>
    @GET("auth/profile")
    suspend fun getProfile() : Response<UserProfile>

    @PUT("auth/profile")
    suspend fun updateProfile(@Body user: User) : Response<MessageResponse>

    @PUT("auth/password")
    suspend fun updatePassword(@Body password: ChangePassword) : Response<MessageResponse>

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
    ): Response<Review>

    @GET("recipe/reviews/all")
    suspend fun getRecipeReviews(
        @Query("recipeId") recipeId: Int
    ): Response<List<Comment>>

    @PUT("recipe/user/purchasedRecipes")
    suspend fun addOrRemoveFreeRecipeToUser(@Query("recipeId") recipeId: Int) : Response<MessageResponse>

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


//    ************* SKŁADNIKI PRZEPISÓW    *********************** //

    @GET("ingredients/all")
    suspend fun getPublicIngredients() : Response<List<Ingredient>>

    @GET("ingredients/user")
    suspend fun getUserIngredients() : Response<List<Ingredient>>

    @POST("ingredients/add")
    suspend fun addUserIngredient() : Response<List<Ingredient>>



//    @PATCH("ingredients/all")
//    suspend fun getPublicIngredients() : Response<List<Ingredient>>



}