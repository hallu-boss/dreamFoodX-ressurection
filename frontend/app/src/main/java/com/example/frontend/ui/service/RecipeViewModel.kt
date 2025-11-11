package com.example.frontend.ui.service

import RecipeCover
import RecipeResponse
import Review
import ReviewRequest
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {

    var recipes by mutableStateOf<List<RecipeCover>>(emptyList())
    var isLoading by mutableStateOf(false)

    var errorMessage by mutableStateOf<String?>(null)
    var responseMmessage by mutableStateOf<String?>(null)


    var isLoadingRating by mutableStateOf(false)
        private set
    var recipeUserRating by mutableStateOf<Int?>(0)
        private set

    fun loadRecipes() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.api.getRecipeCovers(page = 1)
                if (response.isSuccessful) {
                    recipes = response.body()?.recipes ?: emptyList()
                } else {
                    errorMessage = "Błąd: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    var recipeDetail by mutableStateOf<RecipeResponse?>(null)
        private set


    var error by mutableStateOf<String?>(null)
    fun getRecipeById(recipeId: Int, token : String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val response = ApiClient.getApi(token).getRecipe(recipeId)
                if (response.isSuccessful) {
                    recipeDetail = response.body()
                } else {
                    error = "Błąd: ${response.code()}"
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }


    var  userRatingOpinion by  mutableStateOf("")
    fun getRecipeUserRating(recipeId: Int, userId: Int?, token: String) {
        Log.d("Review: ", "getRecipeUserRating  ${recipeId} ${userId}")
        isLoadingRating = true
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
               val response = ApiClient.getApi(token).getRecipeReview(recipeId = recipeId, userId = userId ?: 0)
                if( response.isSuccessful ) {
                    isLoading = false
                    Log.d("Review: ", "getRecipeUserRating  ${response.body()?.recipeId} ${response.body()?.userId} ${response.body()?.rating}")
                    recipeUserRating = response.body()?.rating ?: 0
                    userRatingOpinion = response.body()?.opinion ?: ""
                }
            } catch (e: Exception) {
                error = e.localizedMessage
                Log.d("Review: ", "getRecipeUserRating  ${error}")
            } finally {
                isLoadingRating = false
            }
        }
    }

    fun createRecipeUserRating(reateing: Review, token : String) {
        Log.d("Review: ", "createRecipeUserRating  ${reateing.userId} ${reateing.recipeId} ${reateing.rating}")
        viewModelScope.launch {
            isLoadingRating = true
            error = null
            try {
                val response = ApiClient.getApi(token).createRecipeReviews(reateing)

                if(  response.isSuccessful) {
                    responseMmessage = response.body()?.message
                    Log.d("Review: ", "createRecipeUserRating  ${responseMmessage}")
                    recipeUserRating = reateing.rating
                }
            } catch (e: Exception) {
                error = e.localizedMessage
                Log.d("Review: ", "createRecipeUserRating  ${error}")
            } finally {
                isLoadingRating = false
            }
        }
    }

}
