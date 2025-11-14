package com.example.frontend.ui.screens

import Comment
import RecipeResponse
import Review
import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.firstcomposeap.ui.navigation.main.MainLayout
import com.example.frontend.ui.service.LoginViewModel
import com.example.frontend.ui.service.RecipeViewModel
import  com.example.frontend.ui.components.ErrorPlopup
import com.example.frontend.ui.components.RecipeCard.NetworkImage
import com.example.frontend.ui.components.recipeDetails.IngredientList
import com.example.frontend.ui.components.recipeDetails.StarRating
import com.example.frontend.ui.components.recipeDetails.basicInformation
import com.example.frontend.ui.components.recipeDetails.stepDetail

/**
 * Komponent odpowiedzialny za szczegółowe dane przepisu.
 * Jedynie można dodawać ocenę z komentarzem, nie ma możliwości edycji
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun RecipeDetailScreen(recipeId: String,
                       viewModel: RecipeViewModel, navController: NavHostController,
                       loginViewModel: LoginViewModel
                       ) {
    var selectedItem by remember { mutableStateOf("Strona główna") }

    val tabs = listOf(
        "Przepis",
        "Komentarze"
    )
    var selectedTabIndex by remember { mutableStateOf(0) }

    var mainHeader by remember { mutableStateOf("") }

    MainLayout(
        navController = navController,
        selectedItem = selectedItem,
        onItemSelected = { selectedItem = it }
    ) { innerPadding ->
        Column {
            Spacer(Modifier.height(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Cofnij",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Text(mainHeader, fontSize = 30.sp)
            }

            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontSize = 22.sp) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        mainHeader = "Szczegóły Przepisu"
                        RecipeTab(recipeId, viewModel, navController, loginViewModel)
                    }
                    1 -> {
                        mainHeader = "Komentarze użytkowników"
                        ReviewTab( viewModel)
                    }
                }
            }
        }

    }
}

/**
 * Zakłądka Przepis w szczegółach przepisu, znajdujemy tu zdjęcie, podstawowe informacje o przepisue
 * Możliwość oceny przepisu i komentarz, skłądniki, kroki przepisu potrzebne do przyrządzenia
 */
@Composable
fun RecipeTab( recipeId: String,
               viewModel: RecipeViewModel, navController: NavHostController,
               loginViewModel: LoginViewModel
) {
    val recipeDetail = viewModel.recipeDetail
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage
    val token = loginViewModel.token
    var recipeUserReating by remember { mutableStateOf(viewModel.recipeUserRating ?: 1) }
    val isLoadingRating = viewModel.isLoadingRating


    LaunchedEffect(recipeId, loginViewModel.user?.id, token, viewModel.userRatingOpinion, viewModel.recipeUserRating) {
        token?.let {
            viewModel.getRecipeById(recipeId.toInt(), it)
            viewModel.getRecipeUserRating(
                userId = loginViewModel.user?.id,
                recipeId = recipeId.toInt(),
                token = it
            )
            recipeUserReating = viewModel.recipeUserRating ?: 1
            viewModel.getRecipeReviewAll(recipeId.toInt(), loginViewModel.token ?: "")
        }
    }
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

                    else -> when {
                        isLoadingRating -> CircularProgressIndicator()

                        error != null -> Text(text = "Błąd: $error", color = Color.Red)

                        else -> RecipeDetailContent(recipeDetail, onDismiss = {navController.popBackStack()}, recipeUserReating = recipeUserReating, loginViewModel, viewModel )
                    }
                }
            }
        }
    }
}

/**
 * Zakłakda komentarzy w szczegółach przepisu na jego temat
 */
@Composable
fun ReviewTab(viewModel: RecipeViewModel) {
    Column(modifier = Modifier
        .padding(10.dp)
        .fillMaxSize()) {

        when {
            viewModel.isLoading ->Box(modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()) { CircularProgressIndicator(
                modifier = Modifier.align(
                    Alignment.Center
                )
            )}

            viewModel.errorMessage != null -> Text(
                text = "Błąd: $viewModel.errorMessage",
                color = Color.Red,
            )
            else -> viewModel.reviewList.forEach { review -> SimpleRewiev(review) }

        }
    }

}

/**
 * Komponent do wyświetlania ilości gwiazdek, autora i opinni na dany pprzepis w szczegółach przepisu
 */
@Composable
fun SimpleRewiev(review: Comment) {
    Column (
        modifier = Modifier
            .fillMaxWidth().padding(15.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color.DarkGray).padding(5.dp, 2.dp),
    )
    {
        Spacer(modifier = Modifier.height(5.dp))

        Row {
            var rating by remember {
                mutableStateOf(review.rating) }
            for (i in 1..review.rating) {
                val icon = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star
                val tint = if (i <= rating) MaterialTheme.colorScheme.error else Color.Gray

                Icon(
                    imageVector = icon,
                    contentDescription = "Star $i",
                    tint = tint,
                    modifier = Modifier
                        .padding(4.dp)
                )
            }
            Text(" by ${review.name} ${review.surname}")
        }

        if( !review.opinion.trim().equals("")) {
            Text(review.opinion)
        }
    }



}

@Composable
fun RecipeDetailContent(recipeDetail: RecipeResponse?, onDismiss: () -> Unit , recipeUserReating: Int = 0, loginViewModel: LoginViewModel, recipeViewModel: RecipeViewModel) {
    var userRating by remember { mutableStateOf(recipeUserReating) }

    if (recipeDetail != null) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                recipeDetail.image?.let { imageUrl ->
                    NetworkImage(url = imageUrl, contentDescription = recipeDetail.title)
                }
            }
            item {
                Text(
                    recipeDetail.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            item {
                basicInformation(recipeDetail)
                Spacer(modifier = Modifier.height(10.dp))

            }
            item {
                StarRating(
                    yourStars = userRating,
                    yourOpinion = recipeViewModel.userRatingOpinion ,
                    onRatingChanged = { newRating, userOpinion ->
                    userRating = newRating
                    val newReview = Review(
                        recipeId = recipeDetail.id,
                        rating = newRating,
                        opinion = userOpinion,
                        userId = loginViewModel.user?.id ?: 0
                    )
                        recipeViewModel.createRecipeUserRating(newReview, token = loginViewModel.token
                            ?: "")
                        recipeViewModel.userRatingOpinion = userOpinion
                } )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                IngredientList(recipeDetail.steps)
                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Kroki przepisu",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }


            if( recipeDetail.permission ) {
                itemsIndexed (recipeDetail.steps ?: emptyList() ) { index, step ->
                        stepDetail(step = step, index = index + 1)
                        Spacer(modifier = Modifier.height(15.dp))
                    }
            }
            else {
                item {
                    ErrorPlopup(errorMessage = "Należy zakupić przepis przed zobaczeniem szczegółów",
                        onDismiss = onDismiss)

                }
            }
        }
    } else {
        Text("Ładowanie przepisu...")
    }
}