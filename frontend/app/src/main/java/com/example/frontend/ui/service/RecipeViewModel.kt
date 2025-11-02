package com.example.frontend.ui.service

import RecipeCover
import RecipeResponse
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

    var recipeUserRating by mutableStateOf<Int?>(0)

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

    fun getRecipeUserRating(recipeId: Int, token: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
//                TODO: da oceny val response = ApiClient.getApi(token).getRecipe(recipeId)
                recipeUserRating = 3
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

}
