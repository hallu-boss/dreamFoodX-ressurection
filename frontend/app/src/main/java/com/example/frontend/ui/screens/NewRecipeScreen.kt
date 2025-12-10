package com.example.frontend.ui.screens


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.firstcomposeap.ui.navigation.main.MainLayout
import com.example.frontend.ui.components.FullSizeButton
import com.example.frontend.ui.components.Recipe.newRecipeIgredientsTab
import com.example.frontend.ui.components.Recipe.newRecipeInformationTab
import com.example.frontend.ui.components.Recipe.newRecipeStepsTab
import com.example.frontend.ui.service.LoginViewModel
import com.example.frontend.ui.service.NewRecipeViewModel

@Composable
fun NewRecipeScreen(navController: NavHostController,
               loginViewModel: LoginViewModel = viewModel(),
            newRecipeViewModel : NewRecipeViewModel = viewModel()
) {
    var selectedItem by remember { mutableStateOf("Moje przepisy") }
    val context = LocalContext.current

    val tabs = listOf(
        "Informacje",
        "Składniki",
        "Kroki przepisu"
    )
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        newRecipeViewModel.token = loginViewModel.token
        if( newRecipeViewModel.userIngredientsList.isEmpty()) {
            // TODO pobieranie skłądników użytkownika
        }
     }


    MainLayout(
        navController = navController,
        selectedItem = selectedItem,
        onItemSelected = { selectedItem = it }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Dodaj nowy przepis", fontWeight = FontWeight.Bold, fontSize = 35.sp)
            Spacer(Modifier.height(10.dp))
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontSize = 20.sp) }
                    )
                }
            }

            Column (Modifier
                .verticalScroll(rememberScrollState())
                .weight(12f)
            ) {
                Spacer(Modifier.height(10.dp))
                when (selectedTabIndex) {
                    0 -> {
                        newRecipeInformationTab(newRecipeViewModel = newRecipeViewModel)
                    }
                    1 -> {
                        newRecipeIgredientsTab(newRecipeViewModel = newRecipeViewModel, loginViewModel.user!!.id)
                    }
                    2 -> {
                        newRecipeStepsTab (newRecipeViewModel = newRecipeViewModel)
                    }
                }
                Spacer(Modifier.height(5.dp))
            }
            Box (Modifier.weight(1f)) {
                FullSizeButton("Zapisz przepis",
                onClick = {
                    var isError = false
                    if( !newRecipeViewModel.validateBasicInformation() ) {
                        isError = true
                        Toast.makeText(context, "Należy wypełnić: Nazwę, kategorię oraz dodać obraz", Toast.LENGTH_LONG).show()
                    }

                    if( newRecipeViewModel.stepsListisEmpty() ) {
                        isError = true
                        Toast.makeText(context, "Przepis musi posiadać kroki wykonania", Toast.LENGTH_LONG).show()
                    }

                    if( !isError) {
                        newRecipeViewModel.createRecipe(context)
                        navController.popBackStack()
                    }
                })}
        }
    }

}

