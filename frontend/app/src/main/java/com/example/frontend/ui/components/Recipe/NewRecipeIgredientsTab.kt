package com.example.frontend.ui.components.Recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.ui.components.InputField
import com.example.frontend.ui.components.SelectBox
import com.example.frontend.ui.service.Ingredient
import com.example.frontend.ui.service.NewRecipeViewModel


@Composable
fun newRecipeIgredientsTab (newRecipeViewModel : NewRecipeViewModel, userId: Int) {

    LaunchedEffect(Unit) {
        newRecipeViewModel.getUserIngredients()
    }

    Text("Edytuj swoje składniki", fontSize = 20.sp)
    newRecipeViewModel.userIngredientsList.forEach  {
            skladnik -> IngredientEditCart(
                        ingredient = skladnik,
                        newRecipeViewModel = newRecipeViewModel,
                        onSaveChanges = {ingredient ->
                            val index = newRecipeViewModel.userIngredientsList.indexOfFirst { it.id == ingredient.id }
                            if( index != -1 ) {
                                newRecipeViewModel.userIngredientsList[index] = ingredient
                                newRecipeViewModel.updateIngredient(index)
                            }
                        }
            )
    }
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {newRecipeViewModel.userIngredientsList.add(
            Ingredient(
                id = -newRecipeViewModel.nextUnicateRandIndex(),
                title = "",
                unit = newRecipeViewModel.utilsList.first(),
                category = newRecipeViewModel.ingredientCategoryList.first(),
                ownerId = userId
            )
        ) }
    ) {
        Text("Dodaj nowy składki" )
    }

//    Text("Twoje składniki", fontSize = 20.sp)
//
//    newRecipeViewModel.userIngredientsList.forEach { skladnik ->
//        if( skladnik.ownerId == userId)
//            IngredientCart(skladnik, userId)
//    }

}


@Composable
fun IngredientEditCart(ingredient: Ingredient, newRecipeViewModel: NewRecipeViewModel, onSaveChanges : (Ingredient) -> Unit) {
    var nazwa by remember { mutableStateOf(ingredient.title) }
    var kategoria by remember { mutableStateOf(ingredient.category) }
    var jednostka by remember { mutableStateOf(ingredient.unit) }

    var showButtonSave by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color.DarkGray)
            .padding(10.dp, 4.dp)
    ) {

        InputField(
            label = "Nazwa składnika",
            value = nazwa,
            onValueChange = {nazwa = it
            showButtonSave = true
            }
        )
        SelectBox(
            options = newRecipeViewModel.ingredientCategoryList,
            selectedOption = kategoria,
            onOptionSelected = {
                kategoria = it
                showButtonSave = true
                               },
            label = "Kategoria"
        )

        SelectBox(
            options = newRecipeViewModel.utilsList,
            selectedOption = jednostka,
            onOptionSelected = {
                jednostka = it
                showButtonSave = true
                               },
            label = "Jednostka"
        )

        if( showButtonSave ) {
            Button(
                modifier = Modifier.fillMaxWidth().padding(2.dp),
                onClick = {
                onSaveChanges(Ingredient(
                    id = ingredient.id,
                    title = nazwa,
                    unit = jednostka,
                    category = kategoria,
                    ownerId = ingredient.ownerId
                ) )
                showButtonSave = false
            }) {
                Text("Zapisz zmian składnika")
            }
        }

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
