package com.example.firstcomposeap.ui.navigation.main

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.frontend.R


@Composable
fun BottomMenu(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val items = listOf(
        MenuItem("Strona główna", drawableRes = R.drawable.meal),
        MenuItem("Koszyk", drawableRes = R.drawable.shopping),
        MenuItem("Moje przepisy", drawableRes = R.drawable.burger),
        MenuItem("Konto", drawableRes = R.drawable.profile)
    )

    NavigationBar(tonalElevation = 8.dp) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    when {
                        item.imageVector != null -> Icon(
                            imageVector = item.imageVector,
                            contentDescription = item.title
                        )
                        item.drawableRes != null -> Icon(
                            painter = painterResource(id = item.drawableRes),
                            contentDescription = item.title
                        )
                    }
                },
                label = { Text(item.title) },
                selected = selectedItem == item.title,
                onClick = { onItemSelected(item.title) }
            )
        }
    }
}


data class MenuItem(
    val title: String,
    val imageVector: ImageVector? = null,
    val drawableRes: Int? = null
)
