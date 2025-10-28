package com.example.frontend.ui.service

import LoginRequest
import User
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var user by mutableStateOf<User?>(null)
    var token by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    token = body?.token
                    user = body?.user
                } else {
                    errorMessage = "Błąd logowania: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
        }
    }
}
