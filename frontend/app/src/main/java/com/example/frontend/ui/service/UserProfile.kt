package com.example.frontend.ui.service

import Review
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.Serializable

data class UserProfile(
    val id: Int,
    val name: String,
    val surname: String,
    val email: String,
    val cookingHours: Float,
    val recipes: List<Recipe>,
    val purchasedRecipes: List<Recipe>,
    val ingredients: List<Ingredient>?,
)

data class  Recipe (
    val id   :      Int,
    val title     : String,
    val visible: Boolean,
    val category :  String,
    val userId   :  Int,
    val price:      Float,
    val image    :    String?,
    val createdAt :  String ,
    val updatedAt:  String,
    val description: String,
)

@Serializable
data class ChangePassword(
    val oldPassword: String,
    val newPassword: String,
)

class Ingredient (
    val id: Int,
    title: String,
    unit: String,
    category: String,
    val ownerId: Int? = null
) {

    var title by mutableStateOf(title)
    var unit by mutableStateOf(unit)
    var category by mutableStateOf(category)
}

@Serializable
data class RecipeStep(
    val id: Int,
    val recipeId: Int,
    val ingredientId: Int? = null,
    val stepType: StepType,
    val amount: Float? = null,          // Prisma Decimal → Kotlin Float (lub Double)
    val description: String? = null,
    val mixSpeed: Int? = null,
    val temperature: Int? = null,
    val time: String? = null,
    val title: String,
    val ingredient: Ingredient? = null  // może być zagnieżdżony obiekt
)

@Serializable
enum class StepType {
    ADD_INGREDIENT,
    COOKING,
    DESCRIPTION
}