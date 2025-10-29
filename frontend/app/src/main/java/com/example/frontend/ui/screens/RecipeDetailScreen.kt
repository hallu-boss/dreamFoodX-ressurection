package com.example.frontend.ui.screens

import Ingredient
import RecipeResponse
import RecipeStep
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavHostController
import com.example.firstcomposeap.ui.navigation.main.MainLayout
import com.example.frontend.ui.components.NetworkImage
import com.example.frontend.ui.service.LoginViewModel
import com.example.frontend.ui.service.RecipeViewModel
import  com.example.frontend.ui.components.ErrorPlopup
import com.example.frontend.ui.components.getPrice

@SuppressLint("UnrememberedMutableState")
@Composable
fun RecipeDetailScreen(recipeId: String,
                       viewModel: RecipeViewModel, navController: NavHostController,
                       loginViewModel: LoginViewModel,
                       ) {
    var selectedItem by remember { mutableStateOf("Strona główna") }

    val recipeDetail = viewModel.recipeDetail
    val isLoading = viewModel.isLoading
    val error = viewModel.error
    val token = loginViewModel.token

    LaunchedEffect(recipeId, token) {
        token?.let {
            viewModel.getRecipeById(recipeId.toInt(), it)
        }
    }

    MainLayout(
        navController = navController,
        selectedItem = selectedItem,
        onItemSelected = { selectedItem = it }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Cofnij",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("Szczegóły Przepisu", fontSize = 30.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))


                    Box(modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize()) {
                        when {
                            isLoading -> CircularProgressIndicator(
                                modifier = Modifier.align(
                                    Alignment.Center
                                )
                            )

                            error != null -> Text(
                                text = "Błąd: $error",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )

                            else -> RecipeDetailContent(recipeDetail, onDismiss = {navController.popBackStack()})
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun RecipeDetailContent(recipeDetail: RecipeResponse?, onDismiss: () -> Unit ) {
    if (recipeDetail != null) {
        Column(modifier = Modifier.padding(16.dp)) {
            recipeDetail.image?.let { imageUrl ->
                NetworkImage(url = imageUrl, contentDescription = recipeDetail.title)
            }
            Text(
                recipeDetail.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            basicInformation(recipeDetail)
            Spacer(modifier = Modifier.height(10.dp))

            IngredientList(recipeDetail.steps)
            Spacer(modifier = Modifier.height(10.dp))

            if( recipeDetail.permission) {
//                LazyColumn(
//                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    items (recipeDetail.steps ?: emptyList() ) { step ->
//                        stepDetail(step = step)
//                    }
//                }
            }
            else {
                ErrorPlopup(errorMessage = "Należy zakupić przepis przed zobaczeniem szczegółów",
                    onDismiss = onDismiss)
            }
        }
    } else {
        Text("Ładowanie przepisu...")
    }
}




@Composable
fun stepDetail(step: RecipeStep) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color.DarkGray)
    )
    {

        Text(step.title)
        Spacer(modifier = Modifier.height(5.dp))
        Text(step.stepType)
        Spacer(modifier = Modifier.height(5.dp))
        Text(step.description)
        Spacer(modifier = Modifier.height(5.dp))
        Text("${step.amount}")
        Spacer(modifier = Modifier.height(5.dp))
        Text("${step.temperature}")
        Spacer(modifier = Modifier.height(5.dp))
        Text("${step.mixSpeed}")
        Spacer(modifier = Modifier.height(5.dp))

    }
}

@Composable
fun basicInformation(recipeDetail: RecipeResponse ) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color.DarkGray)
    )
    {
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Podstawowe informacje",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text("Kategoria: ${recipeDetail.category}")
        Text("Autor: ${recipeDetail.author.name} ${recipeDetail.author.surname}")
        Text("Cena: ${getPrice(recipeDetail.price)}")
        Text("Opis: ${recipeDetail.description ?: "Brak opisu" }" )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

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