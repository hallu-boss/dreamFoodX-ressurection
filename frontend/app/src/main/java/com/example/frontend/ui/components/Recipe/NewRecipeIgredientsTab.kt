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
    Text("Edytuj swoje składniki", fontSize = 20.sp)
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

    newRecipeViewModel.userIngredientsList.forEach { skladnik ->
        if( skladnik.ownerId == userId)
            IngredientCart(skladnik, userId)
    }

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
