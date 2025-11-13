package com.example.frontend.ui.service

import Comment
import RecipeCover
import RecipeResponse
import Review
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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

    fun loadRecipes(token: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.getApi(token).getRecipeCovers(page = 1)
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



    fun getRecipeById(recipeId: Int, token : String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = ApiClient.getApi(token).getRecipe(recipeId)
                if (response.isSuccessful) {
                    recipeDetail = response.body()
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

    fun addOrRemoveFreeRecipeToUser(recipeId: Int, token : String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.getApi(token).addOrRemoveFreeRecipeToUser(recipeId)
                if (response.isSuccessful) {
                    responseMmessage = response.body()?.message
                    errorMessage = null
                } else {
                    errorMessage = "Błąd: ${response.errorBody()?.toString()}"
                    responseMmessage = null
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
        }
    }


    var  userRatingOpinion by  mutableStateOf("")
    fun getRecipeUserRating(recipeId: Int, userId: Int?, token: String) {
        Log.d("Review: ", "getRecipeUserRating  ${recipeId} ${userId}")
        isLoadingRating = true
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
               val response = ApiClient.getApi(token).getRecipeReview(recipeId = recipeId, userId = userId ?: 0)
                if( response.isSuccessful ) {
                    isLoading = false
                    Log.d("Review: ", "getRecipeUserRating  ${response.body()?.recipeId} ${response.body()?.userId} ${response.body()?.rating}")
                    recipeUserRating = response.body()?.rating ?: 0
                    userRatingOpinion = response.body()?.opinion ?: ""
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
                Log.d("Review: ", "getRecipeUserRating  ${errorMessage}")
            } finally {
                isLoadingRating = false
            }
        }
    }

    var reviewList = mutableStateListOf<Comment>()
    fun getRecipeReviewAll(recipeId: Int, token: String) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val response = ApiClient.getApi(token).getRecipeReviews(recipeId)
                if( response.isSuccessful ) {
                    reviewList.clear()
                    response.body()?.let { reviewList.addAll(it) }
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
                Log.d("Review: ", "getRecipeUserRating  ${errorMessage}")
            } finally {
                isLoading = false
            }
        }
    }

    fun createRecipeUserRating(reateing: Review, token : String) {
        Log.d("Review: ", "createRecipeUserRating  ${reateing.userId} ${reateing.recipeId} ${reateing.rating}")
        viewModelScope.launch {
            isLoadingRating = true
            errorMessage = null
            try {
                val response = ApiClient.getApi(token).createRecipeReviews(reateing)

                if(  response.isSuccessful) {
                    responseMmessage = response.body()?.message
                    Log.d("Review: ", "createRecipeUserRating  ${responseMmessage}")
                    recipeUserRating = reateing.rating
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
                Log.d("Review: ", "createRecipeUserRating  ${errorMessage}")
            } finally {
                isLoadingRating = false
            }
        }
    }
}
