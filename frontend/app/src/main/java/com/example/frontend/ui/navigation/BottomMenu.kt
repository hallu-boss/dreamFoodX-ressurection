package com.example.firstcomposeap.ui.navigation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp



@Composable
fun BottomMenu(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    val items = listOf(
        MenuItem("Strona główna", Icons.Default.Build),
        MenuItem("Koszyk", Icons.Default.Favorite),
        MenuItem("Moje przepisy", Icons.Default.Person),
        MenuItem("Konto", Icons.Default.DateRange)
    )

    NavigationBar(tonalElevation = 8.dp) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = selectedItem == item.title,
                onClick = { onItemSelected(item.title) }
            )
        }
    }
}

data class MenuItem(val title: String, val icon: ImageVector)
