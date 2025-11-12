package com.example.frontend.ui.service


import User
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    var user by mutableStateOf<User?>(null)

    private val token = MutableStateFlow("")
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)
    var cart by mutableStateOf<Cart?>(null)

    fun setToken(token: String?) {
        this.token.value = token ?: ""
    }


    fun getUserCart() {
        viewModelScope.launch {
            try {
                val response = ApiClient.getApi(token.value).getCart()
                if (response.isSuccessful) {
                    cart = response.body()
                } else {
                    errorMessage = "Błąd logowania: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
        }
    }

    fun addToCart(idRecipe : Int) {
        val req = ApiService.AddToCartRequest(idRecipe)
        viewModelScope.launch {
            try {
                val response = ApiClient.getApi(token.value).addToCart(req)
                if (response.isSuccessful) {
                    successMessage = response.body()?.message
                    errorMessage = null
                } else {
                    errorMessage = "Błąd logowania: ${response.code()}"
                    successMessage = null
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
        }
    }
}
