package com.example.frontend.ui.service

data class Cart (
    val id:        Int,
    val items: List<CartItem>,
    val count:    Int,
    val total: Float,
    val updatedAt: String
)

data class CartItem (
    val id: Int,
    val recipeId: Int,
    val title: String,
    val price: Float,
    val image: String?,
    val author: AuthorRecipe,
    val addedAt: String
)

data class AuthorRecipe (
    val id: Int,
    val name: String,
    val surname: String,
)
