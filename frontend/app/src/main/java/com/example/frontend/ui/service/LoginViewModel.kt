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
    var succesfulMessage by mutableStateOf<String?>(null)
    var userProfile by mutableStateOf<UserProfile?>(null)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    token = body?.token
                    user = body?.user
                } else {
                    errorMessage = "Błąd logowania: ${response.errorBody()?.toString()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
        }
    }

    fun downloadUserProfile() {
        viewModelScope.launch {
            try {
                val response = ApiClient.getApi(token ?: "").getProfile()
                if (response.isSuccessful) {
                    userProfile = response.body()
                } else {
                    errorMessage = "Błąd Pobrania danych użytkownika: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
        }
    }

    fun updateProfile(name: String, surname: String, email: String, cookingHours: Float) {
        val newUser = User(
            id =  user!!.id,
            name =  name,
            surname = surname,
            email = email ,
            cookingHours = cookingHours
        )

        viewModelScope.launch {
            try {
                val response = ApiClient.getApi(token ?: "").updateProfile(newUser)
                if (response.isSuccessful) {
                    succesfulMessage = response.body()!!.message
                    downloadUserProfile()
                } else {
                    errorMessage = "Błąd aktualizacji danych użytkownika: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
        }
    }

    var passwordChangeSuccess by mutableStateOf(false)
    fun updatePassword(oldPassword: String, newPassword:String ) {
        val newPassword = ChangePassword(oldPassword, newPassword)
        viewModelScope.launch {
            try {
                val response = ApiClient.getApi(token ?: "").updatePassword(newPassword)
                if (response.isSuccessful) {
                    succesfulMessage = response.body()?.message
                    passwordChangeSuccess = true
                    errorMessage = null
                } else {
                    errorMessage = response.errorBody()?.string() ?: "Nieznany błąd (${response.code()})"
                    passwordChangeSuccess = false
                }
            }
            catch (e : Exception ) {
                errorMessage = e.localizedMessage
                passwordChangeSuccess = false
            }
        }
    }


}
