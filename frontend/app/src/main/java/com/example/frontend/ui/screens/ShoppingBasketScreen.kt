package com.example.frontend.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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


    MainLayout(
        navController = navController,
        selectedItem = selectedItem,
        onItemSelected = { selectedItem = it }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("To jest ekran koszyka ", fontSize = 40.sp)

            if(cartViewModel.cart!=null) {
                Text( "${cartViewModel.cart!!.id}")
                Text( "${cartViewModel.cart!!.updatedAt}")
                Text( "${cartViewModel.cart!!.total}")
                Text( "${cartViewModel.cart!!.count}")

                Text("Lista produktów w twoim koszyku:")
                cartViewModel.cart!!.items.forEach { produkt ->
                    CartItem(
                        item = produkt,
                        onClick = {}
                    )
                }
            }
        }
    }

}
