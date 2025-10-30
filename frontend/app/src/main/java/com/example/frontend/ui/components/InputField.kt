package com.example.frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


fun validateEmail(input: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    return emailRegex.matches(input)
}

@Composable
fun InputField (
    value: String,
    isError: Boolean = false,
    onValueChange: (String) -> Unit,
    label: String = "Input",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        isError = isError,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .shadow(20.dp, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            cursorColor = Color.Black
        )
    )
}