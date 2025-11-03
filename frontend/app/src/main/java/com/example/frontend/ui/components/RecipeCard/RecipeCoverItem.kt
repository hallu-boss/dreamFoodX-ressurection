package com.example.frontend.ui.components.RecipeCard

import RecipeCover
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.frontend.R
import com.example.frontend.ui.components.icons.Add
import com.example.frontend.ui.components.icons.Add_shopping_cart


private fun getStarColor(ocena: Double): Color {
    return when {
        ocena >= 4.0f -> Color(0xFF4CAF50)
        ocena >= 3.0f -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
}

fun getPrice(cena : Double) : String {
    return when {
        cena == 0.toDouble() -> "Darmowy"
        else -> String.format("%.2f zł", cena)
    }
}

@Composable
fun RecipeCoverItem(recipe: RecipeCover,
                    onClick: () -> Unit
                    ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color.DarkGray)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        recipe.image?.let {imageUrl ->
            NetworkImage(imageUrl,
                contentDescription = recipe.title)
        }
        Text(recipe.title, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(5.dp))
        Column {
            Row {
                Icon(imageVector = Icons.Default.Star,
                    contentDescription = "Ocena użytkowników",
                    tint = getStarColor(recipe.averageRating),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text("${recipe.averageRating}", color = getStarColor(recipe.averageRating))

                Spacer(modifier = Modifier.width(20.dp))
                Text("Kategoria: ${recipe.category}")
                Spacer(modifier = Modifier.width(20.dp))

                Icon(imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Autor",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text("${recipe.author.name} ${recipe.author.surname}")

            }
            Spacer(modifier = Modifier.height(4.dp))


            Row {
                Icon(imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Cena",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(getPrice(recipe.price))

                Spacer(modifier = Modifier.width(40.dp))

                Image(
                    painter = painterResource(id = R.drawable.clock),
                    contentDescription = "Czas gotowania",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(recipe.cookingTime)
                Spacer(modifier = Modifier.width(25.dp))

                if( recipe.price > 0 && !(recipe.isOwned?: false)) {
                    Button(onClick = {
// TODO:    Dodanie koszyka
                    }) {

                        Icon(
                            imageVector = Add_shopping_cart,
                            contentDescription = "Dodaj do koszyka"
                        )
                    }
                }
                else if( recipe.price == 0.0 && !(recipe.isPurchased?: false)){
                    Button(onClick = {
// TODO:    Dodaj do kolekcji
                    }) {
                        Icon(
                            imageVector = Add,
                            contentDescription = "Dodaj do kolekcji przepisów"
                        )
                    }
                }
            }
        }
    }
}


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun NetworkImage(url: String, contentDescription: String?, modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp
    val imageHeight = screenHeightDp / 5

    Image(
        painter = rememberAsyncImagePainter(
            model = url,
            placeholder = painterResource(R.drawable.burger),
            error = painterResource(R.drawable.meal)
        ),
        contentDescription = contentDescription,
        modifier = modifier
            .fillMaxWidth()
            .height(imageHeight),
        contentScale = ContentScale.Crop
    )

}
