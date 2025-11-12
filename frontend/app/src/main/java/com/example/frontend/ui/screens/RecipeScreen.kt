package com.example.frontend.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.firstcomposeap.ui.navigation.main.MainLayout
import com.example.frontend.ui.service.LoginViewModel

@Composable
fun RecipeScreen(navController: NavHostController, loginViewModel: LoginViewModel) {
    var selectedItem by remember { mutableStateOf("Moje przepisy") }
    loginViewModel.downloadUserProfile()
    var profile = loginViewModel.userProfile

    LaunchedEffect(loginViewModel.userProfile) {
        profile = loginViewModel.userProfile
    }

    MainLayout(
        navController = navController,
        selectedItem = selectedItem,
        onItemSelected = { selectedItem = it }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text("To jest ekran strona przepisów ", fontSize = 40.sp)
            if( profile != null ) {
                Column {
                    Text("name ${profile!!.name}")
                    Text("surname ${profile!!.surname}")
                    Text("cookingHours ${profile!!.cookingHours}")
                    Text("purchasedRecipes ${profile!!.purchasedRecipes}")
                    Text("ingredients ${profile!!.ingredients}")
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
