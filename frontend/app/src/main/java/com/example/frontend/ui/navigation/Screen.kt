package com.example.firstcomposeap.ui.navigation.main

sealed class Screen(val route: String) {
    object Home : Screen("Strona główna")
    object Profile : Screen("Konto")
    object Login : Screen("login")
    object Register : Screen("register")
    object Shopping : Screen("Koszyk")
    object Recipes : Screen("Zapisane przepisy")

    object Test : Screen("Testowy")
}