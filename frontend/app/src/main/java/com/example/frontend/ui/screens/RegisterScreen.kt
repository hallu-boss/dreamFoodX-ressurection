package com.example.frontend.ui.screens


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.firstcomposeap.ui.navigation.main.Screen
import com.example.frontend.ui.components.FullSizeButton
import com.example.frontend.ui.components.InputField
import com.example.frontend.ui.components.PasswordInputField
import com.example.frontend.ui.components.validateEmail


@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordError by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = "Rejestracja", fontSize = 38.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            InputField(value = email,
                isError = isError,
                onValueChange = {
                    email = it
                    isError = !validateEmail(it)
                },
                 label = "Email"
            )
            Spacer(modifier = Modifier.height(25.dp))

            PasswordInputField(
                password = password,
                onPasswordChange = {
                    password = it
                    isPasswordError = confirmPassword.isNotEmpty() && password != confirmPassword},
                passwordError = isPasswordError
            )
            Spacer(modifier = Modifier.height(15.dp))
            PasswordInputField(
                password = confirmPassword,
                onPasswordChange = {
                    confirmPassword = it
                    isPasswordError = confirmPassword.isNotEmpty() && password != confirmPassword
                                   },
                passwordError = isPasswordError,
                label = "Potwiedz Hasło"
            )
            Spacer(modifier = Modifier.height(15.dp))



            Spacer(modifier = Modifier.height(16.dp))
            FullSizeButton(
                text = "Zarejestruj się",
                onClick = {
                /* TODO obsługa Rejestracji */
                    navController.navigate(Screen.Home.route)
                    if( isError || email.isEmpty() ) {
                        Toast.makeText(context, "Email musi być w formie example@example.pl", Toast.LENGTH_LONG).show()
                    }
                    else if( isPasswordError || password.isEmpty() || confirmPassword.isEmpty()) {
                        Toast.makeText(context, "Hasła muszą być takie same", Toast.LENGTH_LONG).show()
                    }
                    else {

                    }

                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            FullSizeButton(
                text = "Anuluj",
                onClick = {  navController.navigate(Screen.Login.route) }
            )




        }
    }
}