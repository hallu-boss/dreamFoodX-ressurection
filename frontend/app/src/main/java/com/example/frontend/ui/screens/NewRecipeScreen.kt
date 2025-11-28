package com.example.frontend.ui.screens

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
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
import com.example.frontend.ui.service.LoginViewModel
import com.example.frontend.ui.service.NewRecipeViewModel

@Composable
fun NewRecipeScreen(navController: NavHostController,
               loginViewModel: LoginViewModel = viewModel(),
            newRecipeViewModel : NewRecipeViewModel = viewModel()
) {
    var selectedItem by remember { mutableStateOf("Strona główna") }

    val tabs = listOf(
        "Informacje",
        "Składniki",
        "Kroki przepisu"
    )
    var selectedTabIndex by remember { mutableStateOf(0) }

    MainLayout(
        navController = navController,
        selectedItem = selectedItem,
        onItemSelected = { selectedItem = it }
    ) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
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
                        newRecipeIgredientsTab(newRecipeViewModel = newRecipeViewModel)
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
                    TODO()
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
                label = "Cena",
                value = newRecipeViewModel.cena.toString(),
                onValueChange = {newRecipeViewModel.cena = it.toFloat()},
                modifier = Modifier.weight(4f)

            )
        }


    }

    Spacer(Modifier.height(15.dp))

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
fun newRecipeIgredientsTab (newRecipeViewModel : NewRecipeViewModel) {
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Spacer(Modifier.height(20.dp))
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Spacer(Modifier.height(20.dp))
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")
    Spacer(Modifier.height(20.dp))
    Text("newRecipeIgredientsTab")
    Text("newRecipeIgredientsTab")


}

@Composable
fun newRecipeStepsTab (newRecipeViewModel : NewRecipeViewModel) {
    Text("newRecipeStepsTab")
    Text("newRecipeStepsTab")
}