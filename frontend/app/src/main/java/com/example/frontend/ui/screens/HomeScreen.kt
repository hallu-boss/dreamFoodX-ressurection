package com.example.frontend.ui.screens


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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.firstcomposeap.ui.navigation.main.MainLayout
import com.example.frontend.ui.components.RecipeCoverItem
import com.example.frontend.ui.service.LoginViewModel
import com.example.frontend.ui.service.RecipeViewModel
import kotlin.collections.filter

@Composable
fun HomeScreen(navController: NavHostController,
               loginViewModel: LoginViewModel = viewModel(),
               recipeView: RecipeViewModel = viewModel (),
) {
    var selectedItem by remember { mutableStateOf("Strona główna") }
    val user = loginViewModel.user
    recipeView.loadRecipes()

    val recipes = recipeView.recipes
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }

    val filteredRecipes = recipes.filter { recipe ->
        val matchesCategory = selectedCategory?.let {
            recipe.category.equals(it, ignoreCase = true)
        } ?: true

        val matchesSearch = recipe.title.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesSearch
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
                Text("Dostępne przeisy ", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(25.dp))
                Text("Witaj, ${user?.name} ${user?.surname}", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(20.dp))

                Row ( modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Szukaj po nazwie") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            )
                    )
                    Spacer(Modifier.width(5.dp))
                    CategoryDropdown(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
                }
                Spacer(Modifier.width(15.dp))

                when {
                    recipeView.isLoading -> {
                        Text("Ładowanie przepisów...")
                    }

                    recipeView.errorMessage != null -> {
                        Text(
                            text = "Błąd: ${recipeView.errorMessage}",
                            color = androidx.compose.ui.graphics.Color.Red
                        )
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredRecipes) { recipe ->
                                RecipeCoverItem(recipe = recipe) {
                                    navController.navigate("recipeDetail/${recipe.id}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}


@Composable
fun CategoryDropdown(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("wszystkie", "przekąska", "obiad", "sniadanie", "dodatek", "napój")

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedCategory ?: "Wybierz kategorię")
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { category ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onCategorySelected(if (category == "wszystkie") null else category)
                    },
                    text = { Text(category) }
                )
            }
        }
    }
}
