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

data class Ingredient (
    val id: Int,
    var title: String,
    var unit: String,
    var category: String,
    val ownerId: Int? = null
)

data class UpdateIngredientRequest(
    val category: String,
    val title: String,
    val unit: String
)

data class Step(
    var title: String,
    var stepType: StepType,
    var ingredientId: Int? = null,
    var amount: Double? = null,
    var time: String? = null,
    var temperature: Int? = null,
    var mixSpeed: Int? = null,
    var description: String? = null
)

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