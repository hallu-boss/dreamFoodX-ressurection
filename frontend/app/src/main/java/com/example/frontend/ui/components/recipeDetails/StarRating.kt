package com.example.frontend.ui.components.recipeDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.ui.components.InputField

@Composable
fun StarRating( totalStars: Int = 5,
                modifier: Modifier = Modifier,
                yourStars: Int = 0,
                yourOpinion: String,
                onRatingChanged: ((Int, String) -> Unit)? = null ) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color.DarkGray),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Twoja ocena przepisu ${yourStars}",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        var opinion by remember { mutableStateOf(yourOpinion) }

        InputField(
            value = opinion, isError = false,
            onValueChange = {opinion = it},
            label = "Twoja opinia",
            modifier = Modifier
        )
        Row(modifier = modifier) {
            var rating by remember {
                mutableStateOf(yourStars) }

            for (i in 1..totalStars) {
                val icon = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star
                val tint = if (i <= rating) MaterialTheme.colorScheme.error else Color.Gray

                Icon(
                    imageVector = icon,
                    contentDescription = "Star $i",
                    tint = tint,
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable {
                            rating = i
                            onRatingChanged?.invoke(i, opinion)
                        }
                )
            }
        }

    }
}

