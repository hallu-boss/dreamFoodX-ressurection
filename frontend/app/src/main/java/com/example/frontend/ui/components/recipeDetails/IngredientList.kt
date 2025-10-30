package com.example.frontend.ui.components.recipeDetails

import Ingredient
import RecipeStep
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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


@Composable
fun IngredientList(steps: List<RecipeStep>?) {

    val list: List<Pair<Ingredient, Double>>  = getCumulativeIngredients(steps?: emptyList())

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color.DarkGray)
    )
    {
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Składniki",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        list.forEach { (ingredient, total) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = ingredient.title,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "${"%.2f".format(total)} ${ingredient.unit}",
                    color = Color.LightGray,
                    fontSize = 16.sp
                )
            }
        }

        if (list.isEmpty()) {
            Text(
                text = "Brak składników",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}


fun getCumulativeIngredients(steps: List<RecipeStep>): List<Pair<Ingredient, Double>> {
    return steps
        .filter { it.ingredient != null && it.amount != null }
        .groupBy { it.ingredient!! }
        .map { (ingredient, stepList) ->
            val totalAmount = stepList.sumOf { it.amount ?: 0.0 }
            ingredient to totalAmount
        }
}