package com.example.frontend.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.firstcomposeap.ui.navigation.main.MainLayout
import com.example.frontend.ui.components.Recipe.RecipeCoverItem
import com.example.frontend.ui.components.RecipeFilter
import com.example.frontend.ui.service.CartViewModel
import com.example.frontend.ui.service.LoginViewModel
import com.example.frontend.ui.service.RecipeViewModel

@Composable
fun HomeScreen(navController: NavHostController,
               loginViewModel: LoginViewModel = viewModel(),
               cartViewModel: CartViewModel = viewModel(),
               recipeView: RecipeViewModel,
) {
    var selectedItem by remember { mutableStateOf("Strona główna") }
    val user = loginViewModel.user
    recipeView.loadRecipes(loginViewModel.token ?: "")
    cartViewModel.setToken(loginViewModel.token)
    cartViewModel.getUserCart()
    val context = LocalContext.current
    val recipes = recipeView.recipes
    var filteredRecipes by remember { mutableStateOf(recipes) }
    if (!loginViewModel.isLoggedIn()) {
        navController.navigate("login")
    }

    var nrPage by remember { mutableStateOf(1 ) }

    LaunchedEffect(nrPage) {

    }

    MainLayout(
        navController = navController,
        selectedItem = selectedItem,
        onItemSelected = { selectedItem = it }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Dostępne przepisy ", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(25.dp))
                Text("Witaj, ${user?.name} ${user?.surname}", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(20.dp))

                RecipeFilter( recipes, onFiltered = { filteredRecipes = it })

                when {
                    recipeView.isLoading -> {
                        Text("Ładowanie przepisów...")
                    }

                    recipeView.errorMessage != null -> {
                        Text(
                            text = "Błąd: ${recipeView.errorMessage}",
                            color = Color.Red
                        )
                    }
                    else -> {
//                        PaginationWidget(
//                            currentPage = recipeView.paginationHomePage?.page ?: 1,
//                            totalPages = recipeView.paginationHomePage?.totalPages ?: 2,
//                            onPageChange = { nrPage ->
//                                Log.e("Home", "$nrPage")
//                                recipeView.loadRecipes(loginViewModel.token ?: "", nrPage )
//                            }
//                        )
//                        Spacer(Modifier.height(5.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredRecipes) { recipe ->
                                val isRecipeInCart = cartViewModel.cart?.items?.any { it.recipeId == recipe.id } == true
                                if( recipe.visible)
                                    RecipeCoverItem(
                                    recipe = recipe,
                                    isInCart = isRecipeInCart,
                                    onAddToCart = { recipeId ->
                                        if( !isRecipeInCart)
                                            cartViewModel.addToCart(recipeId)
                                        else
                                            cartViewModel.removeFromCart(recipeId)
                                                  },
                                    onAddToColection = {
                                        idRecipe -> recipeView.addOrRemoveFreeRecipeToUser(idRecipe, loginViewModel.token ?: "")
                                        if( recipeView.responseMmessage != null)
                                            Toast.makeText(context,  recipeView.responseMmessage, Toast.LENGTH_SHORT ).show()
                                        if( recipeView.errorMessage != null)
                                            Toast.makeText(context, recipeView.errorMessage, Toast.LENGTH_SHORT ).show()
                                                       },
                                    onClick = { navController.navigate("recipeDetail/${recipe.id}") },
                                )

                            }
                        }

                    }
                }
            }
        }
    }

}


@Composable
fun PaginationWidget(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Button(
            onClick = { if (currentPage > 1) onPageChange(currentPage - 1) },
            enabled = currentPage > 1
        ) {
            Text("Poprzednia", color = MaterialTheme.colorScheme.secondary)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text("$currentPage / $totalPages")

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages
        ) {
            Text("Następna", color = MaterialTheme.colorScheme.secondary)
        }
    }
}
