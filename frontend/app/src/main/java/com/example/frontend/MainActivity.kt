package com.example.frontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.facebook.CallbackManager
import com.example.firstcomposeap.ui.navigation.main.Screen
import com.example.frontend.ui.screens.*
import com.example.frontend.ui.service.CartViewModel
import com.example.frontend.ui.service.LoginViewModel
import com.example.frontend.ui.service.RecipeViewModel
import com.example.frontend.ui.theme.DreamFoodAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // 1. Tworzymy CallbackManager jako pole w klasie
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val recipeView : RecipeViewModel = viewModel()
            val loginViewModel: LoginViewModel = viewModel()

            // To wywołanie logowania przy starcie jest pewnie do testów, ale OK:
            loginViewModel.login("john.doe@example.com", "password123")

            DreamFoodAppTheme {
                val navController: NavHostController = rememberNavController()
                val cartViewModel: CartViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route // Zmień na Screen.Login jeśli chcesz testować logowanie od razu
                ) {
                    composable(Screen.Register.route) { RegisterScreen(navController) }

                    // 2. Przekazujemy callbackManager do LoginScreen
                    composable(Screen.Login.route) {
                        LoginScreen(
                            navController = navController,
                            viewModel = loginViewModel,
                            callbackManager = callbackManager // <--- PRZEKAZANIE
                        )
                    }

                    composable(Screen.Home.route) { HomeScreen(navController, loginViewModel = loginViewModel, cartViewModel, recipeView = recipeView ) }
                    composable(Screen.Profile.route) { ProfileScreen(navController, loginViewModel = loginViewModel) }
                    composable(Screen.Shopping.route ){ ShoppingBasketScreen(navController, loginViewModel, cartViewModel) }
                    composable(Screen.Recipes.route) { RecipeScreen(navController) }
                    composable(Screen.Test.route) { TestScreen(navController, loginViewModel = loginViewModel) }
                    composable("recipeDetail/{recipeId}") { backStackEntry ->
                        val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
                        RecipeDetailScreen(recipeId, recipeView, navController, loginViewModel)
                    }
                }
            }
        }
    }

    // 3. Kluczowy element: Przekazanie wyniku z Activity do SDK Facebooka
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Najpierw niech Facebook sprawdzi, czy to jego wynik
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}