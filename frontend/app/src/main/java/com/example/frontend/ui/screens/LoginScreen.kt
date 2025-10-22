package com.example.frontend.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.R
import androidx.compose.ui.text.style.TextDecoration
import androidx.navigation.NavHostController
import com.example.firstcomposeap.ui.navigation.main.Screen
import com.example.frontend.ui.components.FullSizeButton
import com.example.frontend.ui.components.InputField
import com.example.frontend.ui.components.LoginBySocialmedia
import com.example.frontend.ui.components.PasswordInputField
import com.example.frontend.ui.components.validateEmail


@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }


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

            Text(text = "Logowanie", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            InputField(value = email,
                isError = isError,
                onValueChange = {
                    email = it
                    isError = !validateEmail(it)
                },
                 label = "Email"
            )
            Spacer(modifier = Modifier.height(16.dp))

            PasswordInputField(
                password = password,
                onPasswordChange = { password = it }
            )
            Spacer(modifier = Modifier.height(5.dp))

            TextButton(onClick = {}, modifier = Modifier.align(Alignment.End)) {
                Text(text = "Przypomij hasło",
                    modifier = Modifier.clickable {},
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 20.sp,
                    textDecoration = TextDecoration.Underline
                )

            }

            Spacer(modifier = Modifier.height(16.dp))
            FullSizeButton(
                text = "Zaloguj się",
                onClick = {
                    /* TODO: Logika logowania */
                    navController.navigate(Screen.Home.route)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            FullSizeButton(
                text = "Zarejestruj się",
                onClick = {  navController.navigate(Screen.Register.route) }
            )


            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Zaloguj się za pomocą")
            Row (modifier = Modifier.fillMaxWidth().padding(40.dp),
                horizontalArrangement = Arrangement.SpaceEvenly) {

                LoginBySocialmedia(id = R.drawable.logo_fb,
                    contentDescription = "Facebook",
                    onClick = {/* TODO obsługa logowanie facebook */})

                LoginBySocialmedia(id = R.drawable.logo_inst,
                    contentDescription = "Instagram",
                    onClick = {/* TODO obsługa logowanie instagram */})
            }


        }
    }
}