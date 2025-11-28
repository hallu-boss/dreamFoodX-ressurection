package com.example.frontend.ui.components.recipeDetails

import RecipeStep
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun stepDetail(step: RecipeStep, index: Int) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color.DarkGray)
            .padding(10.dp, 12.dp)
    )
    {
        Row (modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text("Krok ${index}")
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.error )
                    .padding(horizontal = 1.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(step.title, fontSize = 25.sp)
                Spacer(modifier = Modifier.height(15.dp))
                if (step.stepType == "COOKING") {
                    Row {
                        Text("Czas: ", fontWeight = FontWeight.Bold)
                        Text("${step.time ?: "Czas się nie liczy" } ")
                    }

                    Row {
                        Text("Prędkość ostrzy: ", fontWeight = FontWeight.Bold)
                        Text("${step.mixSpeed ?: " Prędkość nie dotyczy"}")
                    }

                    Row {
                        Text("Temeratura: ", fontWeight = FontWeight.Bold)
                        Text("${step.temperature ?: "0" }")
                    }
                }
                else if ( step.stepType == "DESCRIPTION"){
                    Spacer(modifier = Modifier.height(15.dp))
                    Row {
                        Text("Opis: ", fontWeight = FontWeight.Bold)
                        Text(step.description?: "Brak opisu")
                    }

                }

            }
        }


    }
}