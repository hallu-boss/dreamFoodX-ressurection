package com.example.firstcomposeap.ui.components.profile.profileTab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.example.frontend.ui.service.UserProfile


@Composable
fun EditUserDialog(
    user: UserProfile?,
    onDismiss: () -> Unit,
    onConfirm: (UserProfile) -> Unit
) {
    if (user == null) return

    var imie by remember { mutableStateOf(user.name ?: "") }
    var nazwisko by remember { mutableStateOf(user.surname ?: "") }
    var email by remember { mutableStateOf(user.email ?: "") }


    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edytuj dane użytkownika") },
        text = {
            Column {
                OutlinedTextField(
                    value = imie,
                    onValueChange = { imie = it },
                    label = { Text("Imię") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nazwisko,
                    onValueChange = { nazwisko = it },
                    label = { Text("Nazwisko") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val updatedUser = user.copy(

                    name = imie,
                    surname = nazwisko,
                    email = email
                )
                onConfirm(updatedUser)
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


