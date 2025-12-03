package com.example.frontend.ui.service


import CreatePaymentIntentRequest
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
    var clientSecret by mutableStateOf<String?>(null)
    var publishableKey by mutableStateOf<String?>(null)
    private val token = MutableStateFlow("")
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)
    var cart by mutableStateOf<Cart?>(null)

    fun setToken(token: String?) {
        this.token.value = token ?: ""
    }

    fun createPaymentIntent(amount: Long) { // 'amount' w najmniejszej jednostce (np. groszach)
        // Resetujemy stan, zanim zaczniemy
        clientSecret = null
        publishableKey = null

        viewModelScope.launch {
            try {
                val request = CreatePaymentIntentRequest(amount = amount, currency = "pln")
                val response = ApiClient.getApi(token.value).createPaymentIntent(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        // Ustawiamy klucze do obserwacji przez Composable
                        clientSecret = body.clientSecret
                        publishableKey = body.publishableKey
                        errorMessage = null
                    }
                } else {
                    errorMessage = "Błąd tworzenia płatności: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
        }
    }
    fun getUserCart() {
        viewModelScope.launch {
            try {
                val response = ApiClient.getApi(token.value).getCart()
                if (response.isSuccessful) {
                    cart = response.body()
                } else {
                    errorMessage = "Błąd koszyka: ${response.errorBody()?.string()}"
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
                    errorMessage = "Błąd koszyka: ${response.errorBody()?.string()}"
                    successMessage = null
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
        }
    }

    fun removeFromCart(idRecipe : Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.getApi(token.value).deleteFromCart(idRecipe)
                if (response.isSuccessful) {
                    successMessage = response.body()?.message
                    errorMessage = null
                } else {
                    errorMessage = "Błąd koszyka: ${response.errorBody()?.string()}"
                    successMessage = null
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
        }
    }
}
