package com.example.frontend.ui.service

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class NewRecipeViewModel : ViewModel() {
    val categories = listOf("wszystkie", "przekąska", "obiad", "sniadanie", "dodatek", "napój")
    var isLoading by mutableStateOf(false)

    var token by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf<String?>(null)
    var responseMmessage by mutableStateOf<String?>(null)

    private var rand = Random(0)

     fun nextUnicateRandIndex() : Int {
         var wylosowana = rand.nextInt()
         while ( !isUnicate( wylosowana ) ) {
             wylosowana = rand.nextInt()
         }
        return wylosowana
    }

    private fun isUnicate( wylosowana: Int ) : Boolean {
        val index = userIngredientsList.indexOfFirst { it.id == wylosowana }
        if( index != -1 ) {
            return false
        }
        return true
    }

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
    var publicIngredientsList = mutableStateListOf<Ingredient>()
    var allIngredientsList = mutableStateListOf<Ingredient>()

    fun setSelectableIngrendient() {
        allIngredientsList.clear()
        allIngredientsList.addAll(publicIngredientsList)
        allIngredientsList.addAll(userIngredientsList)
    }


//     kroki porzepisu

    var steps = mutableStateListOf<Step>()
        private set

    fun addNewStep(step: Step ) {
        steps.add(step)
    }

    fun stepsListisEmpty() : Boolean {
        return steps.isEmpty()
    }



    fun moveStep(fromIndex: Int, toIndex: Int) {
        steps.add(toIndex, steps.removeAt(fromIndex))
    }



    fun getPublicIngredients() {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = ApiClient.api.getPublicIngredients()
                if( response.isSuccessful ) {
                    publicIngredientsList.clear()
                    response.body()?.let { publicIngredientsList.addAll(it) }
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
                Log.d("Review: ", "getRecipeUserRating  ${errorMessage}")
            } finally {
                isLoading = false
            }
        }
    }


}