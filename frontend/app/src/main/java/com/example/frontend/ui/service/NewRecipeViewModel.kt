package com.example.frontend.ui.service

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NewRecipeViewModel : ViewModel() {
    val categories = listOf("wszystkie", "przekąska", "obiad", "sniadanie", "dodatek", "napój")

//    Zmienne z informacji

    var nazwa by mutableStateOf("")
    var kategoria by mutableStateOf("")
    var czyPubliczny by mutableStateOf(false)
    var cena by mutableStateOf(0.0f)

    var obraz by mutableStateOf<Uri?>(null)
    fun validateBasicInformation( ) : Boolean {
        if( nazwa=="" || kategoria=="" || obraz == null)
            return false
        return true
    }

//    Zmienne składników
    val utilsList = listOf("g", "ml", "szt")
    val ingredientCategoryList = listOf("Mięso", "Przyprawy", "Warzywa", "Owoce", "Nabiał","Słodycze", "Produkty zbożowe", "Tłuszcze", "Woda", "Orzechy", "Dodatki")
    var userIngredientsList = mutableStateListOf<Ingredient>()


//     kroki porzepisu

    var steps = mutableStateListOf<Step>()
        private set

    init {
        steps.addAll(
            listOf(
                Step("Dodaj mleko", StepType.ADD_INGREDIENT, ingredientId = 1, amount = 200.0),
                Step("Gotuj", StepType.COOKING, time = "10 min", temperature = 100),
                Step("Dodaj przyprawy", StepType.DESCRIPTION, description = "Sól i pieprz")
            )
        )
    }

    fun moveStep(fromIndex: Int, toIndex: Int) {
        steps.add(toIndex, steps.removeAt(fromIndex))
    }


}