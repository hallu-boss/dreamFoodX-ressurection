package com.example.frontend.ui.screens


import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.R
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.firstcomposeap.ui.navigation.main.Screen
import com.example.frontend.ui.components.FullSizeButton
import com.example.frontend.ui.components.InputField
import com.example.frontend.ui.components.LoginBySocialmedia
import com.example.frontend.ui.components.PasswordInputField
import com.example.frontend.ui.components.validateEmail
import com.example.frontend.ui.service.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.util.Log
private const val TAG = "GoogleSignInDebug"
@Composable
fun LoginScreen(navController: NavHostController,
                viewModel: LoginViewModel = viewModel(),
                ) {
    var email by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    val user = viewModel.user
    val errorMessage = viewModel.errorMessage
    val context = LocalContext.current
    val googleClientId = context.getString(R.string.default_web_client_id)

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(googleClientId)
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            Log.d(TAG, "ID Token (do Back-endu): $idToken")

            if (idToken != null) {
                viewModel.loginWithGoogle(idToken)
            } else {
                Toast.makeText(context, "Brak tokena Google. Sprawdź konfigurację Web Client ID w GCP.", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Błąd konfiguracji: Uzyskano konto, ale brak tokena ID.")
            }
        } catch (e: ApiException) {
            val errorMessage = "Logowanie Google nie powiodło się: ${e.statusCode}"
            Log.e(TAG, errorMessage)
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }


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
                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, "Wypełnij wszystkie pola", Toast.LENGTH_LONG).show()
                    } else {
                        viewModel.login(email, password)
                    }
                }
            )
            errorMessage?.let { error ->
                LaunchedEffect(error) {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            }
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
                    contentDescription = "Google",
                    onClick = {
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    })

                LoginBySocialmedia(id = R.drawable.logo_inst,
                    contentDescription = "Instagram",
                    onClick = {/* TODO obsługa logowanie instagram */})
            }


            user?.let {
                LaunchedEffect(it) {
                    Toast.makeText(context, "Witaj, ${it.name}!", Toast.LENGTH_LONG).show()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            errorMessage?.let { error ->
                LaunchedEffect(error) {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}