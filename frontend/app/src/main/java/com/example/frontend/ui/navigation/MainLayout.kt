package com.example.firstcomposeap.ui.navigation.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun MainLayout(
    navController: NavHostController,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomMenu(
                selectedItem = selectedItem,
                onItemSelected = { item ->
                    onItemSelected(item)
                    when (item) {
                        "Zapisane przepisy" -> navController.navigate(Screen.Recipes.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        "Konto" -> navController.navigate(Screen.Profile.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        "Koszyk" -> navController.navigate(Screen.Shopping.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        "Strona główna" -> navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
            content(innerPadding)
    }
}
