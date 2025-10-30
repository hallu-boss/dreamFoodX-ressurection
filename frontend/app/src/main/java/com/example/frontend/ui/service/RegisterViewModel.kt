package com.example.frontend.ui.service

import RegisterRequest
import RegisterResponse
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel : ViewModel() {

    var registerState by mutableStateOf<RegisterResponse?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun registerUser(name: String, surname: String, email: String, password: String, cookingHours: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.register(
                    RegisterRequest(name, surname, email, password, cookingHours)
                )
                if (response.isSuccessful) {
                    registerState = response.body()
                } else {
                    errorMessage = "Błąd: ${response.code()} - ${response.message()}"
                }
            } catch (e: HttpException) {
                errorMessage = "Błąd HTTP: ${e.message}"
            } catch (e: Exception) {
                errorMessage = "Nieoczekiwany błąd: ${e.localizedMessage}"
            }
        }
    }
}
