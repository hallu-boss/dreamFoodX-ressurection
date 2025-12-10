package com.example.frontend.ui.components.Recipe

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.ui.components.InputField
import com.example.frontend.ui.components.SelectBox
import com.example.frontend.ui.service.NewRecipeViewModel


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
