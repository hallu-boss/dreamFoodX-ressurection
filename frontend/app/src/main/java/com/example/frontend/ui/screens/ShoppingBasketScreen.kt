package com.example.frontend.ui.screens


import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.firstcomposeap.ui.navigation.main.MainLayout
import com.example.frontend.ui.components.Cart.CartItem
import com.example.frontend.ui.components.FullSizeButton
import com.example.frontend.ui.service.CartViewModel
import com.example.frontend.ui.service.LoginViewModel

@Composable
fun ShoppingBasketScreen(navController: NavHostController,
                         loginViewModel: LoginViewModel,
                          cartViewModel: CartViewModel = viewModel()
) {
    var selectedItem by remember { mutableStateOf("koszyk") }

    cartViewModel.setToken(loginViewModel.token)
    cartViewModel.getUserCart()
    val context = LocalContext.current
    var changeElement by remember { mutableStateOf(false) }

    LaunchedEffect(changeElement) {
        cartViewModel.getUserCart()
    }

    MainLayout(
        navController = navController,
        selectedItem = selectedItem,
        onItemSelected = { selectedItem = it }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(5.dp))
                Text("Koszyk", fontSize = 40.sp)

                Spacer(Modifier.height(16.dp))


                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    if(cartViewModel.cart != null) {
                        Spacer(modifier = Modifier.height(25.dp))
                        if( (cartViewModel.cart?.items?.count() ?: 0) > 0) {
                            cartViewModel.cart!!.items.forEach { produkt ->
                                CartItem(
                                    item = produkt,
                                    onClick = { produktId ->
                                        cartViewModel.removeFromCart(produktId)
                                        changeElement = !changeElement
                                        cartViewModel.successMessage?.let {
                                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                        }
                                        cartViewModel.errorMessage?.let {
                                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                )
                            }

                            printTotalPrice(cartViewModel)
                            Spacer(Modifier.height(16.dp))

                            FullSizeButton(
                                text = "Przejdz do podsumowania",
                                onClick = {},
                                modifier = Modifier
                                    .padding(8.dp)
                            )

                        }
                        else {
                            Text("Brak produktów w koszyku", fontSize = 30.sp)
                        }

                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

}



@Composable
fun printTotalPrice(cartViewModel: CartViewModel ) {
    Spacer(Modifier.height(35.dp))
    HorizontalDivider(
        color = MaterialTheme.colorScheme.primary,
        thickness = 2.dp,
        modifier = Modifier.fillMaxWidth()
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Suma: ",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            "${cartViewModel.cart?.total ?: 0} PLN",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
    }
}