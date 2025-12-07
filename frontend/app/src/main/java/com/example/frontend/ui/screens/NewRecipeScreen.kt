package com.example.frontend.ui.screens

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.firstcomposeap.ui.navigation.main.MainLayout
import com.example.frontend.ui.components.FullSizeButton
import com.example.frontend.ui.components.InputField
import com.example.frontend.ui.components.SelectBox
import com.example.frontend.ui.service.Ingredient
import com.example.frontend.ui.service.LoginViewModel
import com.example.frontend.ui.service.NewRecipeViewModel
import com.example.frontend.ui.service.Step
import com.example.frontend.ui.service.StepType

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
        if( newRecipeViewModel.userIngredientsList.isEmpty()) {
            newRecipeViewModel.userIngredientsList.addAll(
                listOf(
                    Ingredient(
                        title = "Ziemniaki",
                        unit = "g",
                        category = "Warzywa",
                        id = 1,
                        ownerId = 11
                    ),
                    Ingredient(
                        title = "Jogurt grecki",
                        unit = "g",
                        category = "Nabiał",
                        id = 1,
                        ownerId = 11
                    ),
                    Ingredient(
                        title = "Ogórek",
                        unit = "szt",
                        category = "Warzywa",
                        id = 1,
                        ownerId = 19
                    ),
                    Ingredient(
                        title = "Spaghetti",
                        unit = "g",
                        category = "Produkty zbożowe",
                        id = 1,
                        ownerId = 11
                    )
                )
            )
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

                    if( !isError) {
                        TODO() // wysyłanie na serwer nowego przepisu
                    }
                })}
        }
    }

}

@Composable
fun newRecipeInformationTab(newRecipeViewModel: NewRecipeViewModel) {
    val context = LocalContext.current

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        newRecipeViewModel.obraz = uri
    }
    Text("Nazwa przepisu", fontSize = 20.sp)
    InputField(
        label = "Nazwa",
        value = newRecipeViewModel.nazwa,
        onValueChange = { newRecipeViewModel.nazwa = it }
    )
    Spacer(Modifier.height(15.dp))
    Text("Wybierz kategorię", fontSize = 20.sp)
    SelectBox(
        options = newRecipeViewModel.categories,
        selectedOption = newRecipeViewModel.kategoria,
        onOptionSelected = { newRecipeViewModel.kategoria = it },
        label = "Wybierz kategorię"
    )

    Spacer(Modifier.height(15.dp))
    Text("Czy chcesz by przepis był widoczny dla innych?", fontSize = 20.sp)
    var price by remember { mutableStateOf(newRecipeViewModel.cena.toString()) }
    Row(Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier.weight(2f),
            onClick = {newRecipeViewModel.czyPubliczny = !newRecipeViewModel.czyPubliczny}
        ) {
            Text(if (newRecipeViewModel.czyPubliczny)  "Publiczny" else "Prywatny" )
        }
        if( newRecipeViewModel.czyPubliczny) {
            Box(modifier = Modifier.weight(1f))
            InputField(
                label = "Cena (PLN)",
                value = price,
                onValueChange = {
                    price = it
                    if( price != "")
                        newRecipeViewModel.cena = price.toFloat()
                                },
                modifier = Modifier.weight(4f)

            )
        }


    }

    Spacer(Modifier.height(15.dp))
    Text("Wybierz grafikę by zachęcić użytkowników", fontSize = 20.sp)
    Button(onClick = { pickImage.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
        Text("Wybierz obraz")
    }

    Text("Wybrany obraz: ")

    val bitmap = remember(newRecipeViewModel.obraz) {
        newRecipeViewModel.obraz?.let { uri ->
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}


@Composable
fun newRecipeIgredientsTab (newRecipeViewModel : NewRecipeViewModel, userId: Int) {
    Text("Edytuj swoje skłądniki", fontSize = 20.sp)
    newRecipeViewModel.userIngredientsList.forEach  {
            skladnik -> IngredientEditCart(skladnik, newRecipeViewModel)
    }
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {newRecipeViewModel.userIngredientsList.add(
        Ingredient(
            id = -1,
            title = "",
            unit = newRecipeViewModel.utilsList.first(),
            category = newRecipeViewModel.ingredientCategoryList.first(),
            ownerId = userId
        )
        ) }
    ) {
        Text("Dodaj nowy składki" )
    }

    Text("Twoje składniki", fontSize = 20.sp)

//    newRecipeViewModel.userIngredientsList.forEach { skladnik ->
//        if( skladnik.ownerId == userId)
//            IngredientCart(skladnik, userId)
//    }

}


@Composable
fun IngredientEditCart(ingredient: Ingredient, newRecipeViewModel: NewRecipeViewModel) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color.DarkGray)
            .padding(10.dp, 4.dp)
    ) {

        InputField(
            label = ingredient.title,
            value = ingredient.title,
            onValueChange = {
                ingredient.title = it
                newRecipeViewModel.nazwa = ingredient.title
            }
        )
        SelectBox(
            options = newRecipeViewModel.ingredientCategoryList,
            selectedOption = ingredient.category,
            onOptionSelected = {ingredient.category = it},
            label = ingredient.category
        )

        SelectBox(
            options = newRecipeViewModel.utilsList,
            selectedOption = ingredient.unit,
            onOptionSelected = {ingredient.unit = it},
            label = ingredient.unit
        )


    }
}

@Composable
fun IngredientCart(ingredient: Ingredient, userId: Int) {
    if( userId != ingredient.ownerId)
        return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color.DarkGray)
            .padding(10.dp, 4.dp)

    ) {
        Text("${ingredient.title}", modifier = Modifier.weight(3f))
        Text("${ingredient.category}", modifier = Modifier.weight(2.2f))
        Text("${ingredient.unit}", modifier = Modifier.weight(1f))
    }
}

@Composable
fun newRecipeStepsTab(newRecipeViewModel: NewRecipeViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "newRecipeStepsTab",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
//        StepsList(newRecipeViewModel)
//        val steps =  newRecipeViewModel.steps
//        steps.forEachIndexed { index, krok ->
//            StepCard(
//                step = krok,
//                modifier = Modifier
//                    .pointerInput(Unit) {
//                        detectTapGestures(
//                            onLongPress = {
//                                if (index < steps.size - 1) {
//                                    newRecipeViewModel.moveStep(index, index + 1)
//                                }
//                            }
//                        )
//                    }
//            )
//        }
        StepsList(newRecipeViewModel)
    }
}


@Composable
fun StepCard(step: Step, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (step.stepType) {
                StepType.ADD_INGREDIENT -> {
                    Text("Typ: Dodaj składnik")
                    Text("ID składnika: ${step.ingredientId}")
                    Text("Ilość: ${step.amount}")
                }
                StepType.COOKING -> {
                    Text("Typ: Gotowanie")
                    step.time?.let { Text("Czas: $it") }
                    step.temperature?.let { Text("Temperatura: $it °C") }
                    step.mixSpeed?.let { Text("Prędkość mieszania: $it") }
                }
                StepType.DESCRIPTION -> {
                    Text("Typ: Opis")
                    step.description?.let { Text(it) }
                }
            }
        }
    }
}



@Composable
fun StepsList(newRecipeViewModel: NewRecipeViewModel) {
    val steps = newRecipeViewModel.steps

    LazyColumn {
        itemsIndexed(
            items = steps,
            key = { _, step -> step.title } // Każdy krok musi mieć unikalny klucz
        ) { index, step ->

            StepCard(
                step = step,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                // Prosty swap z następnym elementem
                                if (index < steps.size - 1) {
                                    newRecipeViewModel.moveStep(index, index + 1)
                                }
                            }
                        )
                    }
            )
        }
    }
}