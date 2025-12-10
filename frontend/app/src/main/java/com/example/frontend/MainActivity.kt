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
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val recipeView : RecipeViewModel = viewModel()
            val loginViewModel: LoginViewModel = viewModel()
            // Testowy użytkownik
//            loginViewModel.login("john.doe@example.com", "password123")
            //  loginViewModel.login("michalkaniewski1997@gmail.com", "michalkaniewski1997@gmail.com")


            DreamFoodAppTheme {
                val navController: NavHostController = rememberNavController()
                val cartViewModel: CartViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route
                ) {
                    composable(Screen.Register.route) { RegisterScreen(navController) }
                    composable(Screen.Login.route) {
                        LoginScreen(
                            navController = navController,
                            viewModel = loginViewModel,
                            callbackManager = callbackManager
                        )
                    }

                    composable(Screen.Home.route) { HomeScreen(navController, loginViewModel = loginViewModel, cartViewModel, recipeView = recipeView ) }
                    composable(Screen.Profile.route) { ProfileScreen(navController, userViewModel = loginViewModel) }
                    composable(Screen.Shopping.route ){ ShoppingBasketScreen(navController, loginViewModel, cartViewModel) }
                    composable(Screen.Recipes.route) { RecipeScreen(navController, loginViewModel, recipeView = recipeView) }
                    composable(Screen.Test.route) { TestScreen(navController, loginViewModel = loginViewModel) }

                    composable(Screen.NewRecipe.route) { NewRecipeScreen(navController
                        , loginViewModel = loginViewModel
                    , onClose = {
                        recipeView.loadRecipes(loginViewModel.token ?: "")
                        navController.popBackStack()}) }

                    composable("recipeDetail/{recipeId}") { backStackEntry ->
                        val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
                        RecipeDetailScreen(recipeId, recipeView, navController, loginViewModel)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}