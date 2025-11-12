package com.example.frontend.ui.components.Cart



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.ui.components.icons.Delete
import com.example.frontend.ui.service.CartItem
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import com.example.frontend.ui.theme.greenDark

@Composable
fun CartItem(
    item: CartItem,
    onClick : () -> Unit
) {
    Row(Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .background(Color.DarkGray)
        .shadow(2.dp, RectangleShape),
        verticalAlignment = Alignment.CenterVertically
        ) {
        Row (
            modifier = Modifier
                .weight(8f)
                .padding(2.dp)
        ) {
            Text("\"${item.title}\" -", fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
            Text("- ${item.author.name} ${item.author.surname} ")
        }
        Box(modifier = Modifier.weight(2f) ) {
            Text("${item.price}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        FloatingActionButton(
            onClick = onClick,
            containerColor  = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(25.dp)
                .weight(1f)
        ) {
            Icon(imageVector = Delete,
                tint = Color.White,
                contentDescription = "Usuń produkt z koszyka")
        }




    }
}