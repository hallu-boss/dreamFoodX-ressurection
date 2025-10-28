package com.example.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.firstcomposeap.ui.navigation.main.Screen
import com.example.frontend.ui.screens.HomeScreen
import com.example.frontend.ui.screens.LoginScreen
import com.example.frontend.ui.screens.ProfileScreen
import com.example.frontend.ui.screens.RecipeScreen
import com.example.frontend.ui.screens.RegisterScreen
import com.example.frontend.ui.screens.ShoppingBasketScreen
import com.example.frontend.ui.screens.TestScreen
import com.example.frontend.ui.service.LoginViewModel
import com.example.frontend.ui.theme.DreamFoodAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val loginViewModel: LoginViewModel = viewModel()
            loginViewModel.login("testUser@testUser.testUser", "testUser")

            DreamFoodAppTheme {
                val navController: NavHostController = rememberNavController()


                NavHost(
                    navController = navController,
                    startDestination = Screen.Test.route
                ) {
                    composable(Screen.Register.route) { RegisterScreen(navController) }
                    composable(Screen.Login.route) { LoginScreen(navController, viewModel = loginViewModel) }
                    composable(Screen.Home.route) { HomeScreen(navController, loginViewModel = loginViewModel) }
                    composable(Screen.Profile.route) { ProfileScreen(navController) }
                    composable(Screen.Shopping.route ){ ShoppingBasketScreen(navController) }
                    composable(Screen.Recipes.route) { RecipeScreen(navController) }
                    composable(Screen.Test.route) { TestScreen(navController, loginViewModel = loginViewModel) }
                }
            }
        }
    }
}