package com.example.frontend.ui.components.Recipe

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.frontend.ui.components.IngredientSelectBox
import com.example.frontend.ui.components.SelectBox
import com.example.frontend.ui.service.Ingredient
import com.example.frontend.ui.service.NewRecipeViewModel
import com.example.frontend.ui.service.Step
import com.example.frontend.ui.service.StepType
import kotlin.math.roundToInt


@SuppressLint("UnrememberedMutableState")
@Composable
fun NewRecipeStepDialog(
    onDismiss: () -> Unit,
    onConfirm: (Step) -> Unit,
    newRecipeViewModel: NewRecipeViewModel
) {
    var errorTytul by remember { mutableStateOf(false) }
    val stepType = listOf("Dodaj składnik", "Gotowanie", "Opisowy")
    var selectedOption = remember { mutableStateOf("") }

    var tytul by remember { mutableStateOf("") }

    var retStep by remember { mutableStateOf( value = Step(
        title= tytul,
        stepType = StepType.ADD_INGREDIENT
    )
    ) }

    LaunchedEffect(Unit ) {
        newRecipeViewModel.getPublicIngredients()
        Log.e("allIngredientsList", "${newRecipeViewModel.allIngredientsList.size}")

    }


    var selectedCategory by remember { mutableStateOf("") }
    val ingredientInCategory = remember { mutableStateListOf<Ingredient>() }

    LaunchedEffect(selectedCategory) {
        if( selectedOption.value == stepType[0]) {
            newRecipeViewModel.setSelectableIngrendient()

            val filteredIngredients = newRecipeViewModel.allIngredientsList
                .filter { it.category == selectedCategory }
                .map { ingredient ->
                    Ingredient(
                        id = ingredient.id,
                        title = ingredient.title,
                        unit = ingredient.unit,
                        category = ingredient.category,
                        ownerId = ingredient.ownerId
                    )
                }.sortedBy { it.title }


            ingredientInCategory.clear()
            ingredientInCategory.addAll(filteredIngredients)
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Dodaj nowy krok przepisu") },
        text = {
            Column {
                SelectBox(
                    options = stepType,
                    selectedOption = selectedOption.value,
                    onOptionSelected = {selectedOption.value = it},
                    label = "Wybierz typ kroku"
                )
                Spacer(Modifier.height(10.dp))


                OutlinedTextField(
                    value = tytul,
                    isError = errorTytul,
                    onValueChange = { tytul = it
                        if( tytul!= "") {
                            errorTytul = false
                        }
                    },
                    label = { Text("Tytuł kroku") },
                    modifier = Modifier.fillMaxWidth()
                )


                if( selectedOption.value == stepType[0]) {
                    retStep.stepType = StepType.ADD_INGREDIENT


                    SelectBox(
                        options = newRecipeViewModel.ingredientCategoryList,
                        selectedOption = selectedCategory,
                        onOptionSelected = { selectedCategory = it},
                        label = "Wybierz kategorię produktu"
                    )

                    var selectedIngredient by remember { mutableStateOf<Ingredient?>(null) }

                    IngredientSelectBox(
                        options = ingredientInCategory,
                        selectedOption = selectedIngredient,
                        onOptionSelected = { selectedIngredient = it },
                        label = "Wybierz składnik"
                    )

                    var amount by remember { mutableStateOf(0.0) }
                    var amountText by remember { mutableStateOf(amount.toString()) }

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { text ->
                            amountText = text

                            val parsed = text.toDoubleOrNull()
                            if (parsed != null) {
                                amount = parsed
                            }
                        },
                        label = { Text("Ilość") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if( selectedIngredient != null)
                        retStep.ingredientId = selectedIngredient!!.id
                    retStep.amount = amount

                }
                else if( selectedOption.value == stepType[1] ) {
                    Spacer(Modifier.height(10.dp))
                    retStep.stepType = StepType.COOKING
                    var czas by remember { mutableStateOf(TextFieldValue("")) }
                    TimeField(
                        value = czas,
                        onValueChange = {czas = it}
                    )
                    Spacer(Modifier.height(10.dp))

                    var temp by remember { mutableStateOf(1) }
                    FiveLevelSlider(
                        value = temp,
                        onValueChange = { temp = it},
                        label = "Temperatura: "
                    )
                    Spacer(Modifier.height(10.dp))

                    var sppedBlade by remember { mutableStateOf(1) }
                    FiveLevelSlider(
                        value = sppedBlade,
                        onValueChange = { sppedBlade = it},
                        label = "Prędkość ostrzy: "
                    )

                    retStep.time = czas.text
                    retStep.temperature = temp
                    retStep.mixSpeed = sppedBlade
                }
                else if( selectedOption.value == stepType[2] ) {
                    retStep.stepType = StepType.DESCRIPTION
                    var opis by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = opis,
                        onValueChange = { opis = it },
                        label = { Text("Opis kroku") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    retStep.description = opis
                }

            }
        },
        confirmButton = {
            TextButton(onClick = {
                retStep.title = tytul
                if( tytul != "") {
                    onConfirm(retStep)
                }
                else {
                    errorTytul = true
                }

            }) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Anuluj")
            }
        }
    )
}

@Composable
fun FiveLevelSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String = "Poziom"
) {
    val labels = listOf(
        "Bardzo niska",
        "Niska",
        "Średnia",
        "Wysoka",
        "Bardzo wysoka"
    )

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {

        Text(
            text = "$label: ${labels[value - 1]}",
            style = MaterialTheme.typography.bodyLarge
        )

        Slider(
            value = value.toFloat(),
            onValueChange = { newValue ->
                onValueChange(newValue.roundToInt().coerceIn(1, 5))
            },
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun TimeField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String = "Czas (mm:ss)"
) {
    TextField(
        value = value,
        onValueChange = { input ->

            val digits = input.text.filter { it.isDigit() }

            val limited = digits.take(6)

            val formatted = when {
                limited.length <= 2 -> limited   // sekundy, ale bez :
                else -> {
                    val minutes = limited.dropLast(2)
                    val seconds = limited.takeLast(2)
                    "$minutes:$seconds"
                }
            }

            val valid = if (formatted.contains(":")) {
                val parts = formatted.split(":")
                val minutes = parts[0]
                val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0

                val safeSeconds = seconds.coerceIn(0, 59)
                "$minutes:${"%02d".format(safeSeconds)}"
            } else formatted

            onValueChange(
                TextFieldValue(
                    text = valid,
                    selection = TextRange(valid.length)
                )
            )
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}
