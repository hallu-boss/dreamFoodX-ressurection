package com.example.frontend.ui.components.Recipe

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.frontend.ui.service.NewRecipeViewModel
import com.example.frontend.ui.service.Step
import com.example.frontend.ui.service.StepType


@Composable
fun newRecipeStepsTab(newRecipeViewModel: NewRecipeViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        val steps =  newRecipeViewModel.steps
        steps.forEachIndexed { index, krok ->
            StepCard(
                step = krok,
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                if (index < steps.size - 1) {
                                    newRecipeViewModel.moveStep(index, index + 1)
                                }
                            }
                        )
                    }
            )
        }
    }
    Button(onClick = {showDialog = true },
        modifier = Modifier.fillMaxWidth())
    {
        Text("Dodaj krok")
    }


    if (showDialog) {
        NewRecipeStepDialog (
            onDismiss = { showDialog = false },
            onConfirm = {
                    step -> newRecipeViewModel.addNewStep(step)
                showDialog = false
            },
            newRecipeViewModel = newRecipeViewModel
        )
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
                    Text("Ilość: ${step.amount}")
                }
                StepType.COOKING -> {
                    step.time?.let { Text("Czas: $it") }
                    step.temperature?.let { Text("Temperatura: $it") }
                    step.mixSpeed?.let { Text("Prędkość mieszania: $it") }
                }
                StepType.DESCRIPTION -> {
                    step.description?.let { Text(it) }
                }
            }
        }
    }
}



