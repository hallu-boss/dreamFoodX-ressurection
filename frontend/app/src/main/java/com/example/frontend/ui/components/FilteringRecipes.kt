package com.example.frontend.ui.components

import RecipeCover
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.collections.filter
import kotlin.text.equals


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


@Composable
fun RecipeFilter(
    recipes: List<RecipeCover>,
    onFiltered: (List<RecipeCover>) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }


    LaunchedEffect(searchQuery, selectedCategory, recipes) {
        val filtered = recipes.filter { recipe ->
            val matchesCategory = selectedCategory?.let {
                recipe.category.equals(it, ignoreCase = true)
            } ?: true

            val matchesSearch = recipe.title.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
        onFiltered(filtered)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Szukaj po nazwie") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
            ),
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        CategoryDropdown(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )
    }
}
