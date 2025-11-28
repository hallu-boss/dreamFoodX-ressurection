package com.example.frontend.ui.service

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NewRecipeViewModel : ViewModel() {
    val categories = listOf("wszystkie", "przekąska", "obiad", "sniadanie", "dodatek", "napój")

//    Zmienne z informacji

    var nazwa by mutableStateOf("")
    var kategoria by mutableStateOf(false)
    var czyPubliczny by mutableStateOf(false)
    var cena by mutableStateOf(0.0f)

    var obraz by mutableStateOf<Uri?>(null)


}