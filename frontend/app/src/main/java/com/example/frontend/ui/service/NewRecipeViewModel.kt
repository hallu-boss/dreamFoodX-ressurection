package com.example.frontend.ui.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NewRecipeViewModel : ViewModel() {

    var nazwa by mutableStateOf("")

}