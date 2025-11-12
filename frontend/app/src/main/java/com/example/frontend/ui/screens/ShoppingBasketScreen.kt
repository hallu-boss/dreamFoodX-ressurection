package com.example.frontend.ui.screens


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.firstcomposeap.ui.navigation.main.MainLayout
import com.example.frontend.ui.components.Cart.CartItem
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Text("To jest ekran koszyka ", fontSize = 40.sp)
            if(cartViewModel.cart!=null) {

                Text("Lista produktów w twoim koszyku:")
                cartViewModel.cart!!.items.forEach { produkt ->
                    CartItem(
                        item = produkt,
                        onClick = {produktId -> cartViewModel.removeFromCart(produktId)
                            changeElement = !changeElement // pobranie nowej listy po zmianie
                            if( cartViewModel.successMessage != null)
                                Toast.makeText(context, cartViewModel.successMessage, Toast.LENGTH_LONG ).show()
                            if( cartViewModel.errorMessage != null)
                                Toast.makeText(context, cartViewModel.errorMessage, Toast.LENGTH_LONG ).show()
                        }
                    )
                }
            }
            else {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            }
        }
    }

}
