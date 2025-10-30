package com.example.frontend.ui.components.recipeDetails

import RecipeResponse
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.ui.components.getPrice

@Composable
fun basicInformation(recipeDetail: RecipeResponse ) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color.DarkGray)
    )
    {
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Podstawowe informacje",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text("Kategoria: ${recipeDetail.category}")
        Text("Autor: ${recipeDetail.author.name} ${recipeDetail.author.surname}")
        Text("Cena: ${getPrice(recipeDetail.price)}")
        Text("Opis: ${recipeDetail.description ?: "Brak opisu" }" )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

