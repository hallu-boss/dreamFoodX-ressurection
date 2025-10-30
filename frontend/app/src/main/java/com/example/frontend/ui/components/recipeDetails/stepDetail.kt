package com.example.frontend.ui.components.recipeDetails

import RecipeStep
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun stepDetail(step: RecipeStep) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color.DarkGray)
    )
    {
        Text(step.title)
        Spacer(modifier = Modifier.height(5.dp))
        Text(step.stepType)
        Spacer(modifier = Modifier.height(5.dp))
        Text(step.description?: "Brak opisu")
        Spacer(modifier = Modifier.height(5.dp))
        Text("${step.amount ?: "Brak ilość" }")
        Spacer(modifier = Modifier.height(5.dp))
        Text("${step.time ?: "Czas się nie liczy" } s.")
        Spacer(modifier = Modifier.height(5.dp))
        Text("${step.temperature ?: "Brak temperatury"}")
        Spacer(modifier = Modifier.height(5.dp))
        Text("${step.mixSpeed ?: " Prędkość nie dotyczy"}")
        Spacer(modifier = Modifier.height(5.dp))

    }
}