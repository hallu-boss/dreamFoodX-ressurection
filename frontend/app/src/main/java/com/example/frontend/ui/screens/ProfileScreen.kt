package com.example.frontend.ui.screens


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.firstcomposeap.ui.components.UniversalEditCard
import com.example.firstcomposeap.ui.components.profile.profileTab.ChangePasswordDialog
import com.example.firstcomposeap.ui.components.profile.profileTab.EditUserDialog
import com.example.firstcomposeap.ui.navigation.main.MainLayout
import com.example.frontend.ui.service.LoginViewModel

@Composable
fun ProfileScreen(navController: NavHostController,
                  userViewModel: LoginViewModel
) {
    val context = LocalContext.current
    var selectedItem by remember { mutableStateOf("Konto") }
    userViewModel.downloadUserProfile()

    val message = userViewModel.errorMessage
    val errorMessage = userViewModel.succesfulMessage

    var userProfile = userViewModel.userProfile
    LaunchedEffect(userViewModel.userProfile) {
        userProfile = userViewModel.userProfile
    }
    var showDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    MainLayout(
        navController = navController,
        selectedItem = selectedItem,
        onItemSelected = { selectedItem = it }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if( userProfile != null) {
                UniversalEditCard(
                    data = {
                        Text("imie: ${userProfile?.name}", fontSize = 30.sp)
                        Text("nazwisko: ${userProfile?.surname}", fontSize = 30.sp)
                        Spacer(modifier = Modifier.height(5.dp))

                        Text("email: ${userProfile?.email}", fontSize = 25.sp)

                    },
                    onClick = { showDialog = true }
                )

                UniversalEditCard(
                    data = {
                        Text("Zmień stwoje hasło", fontSize = 30.sp)

                    },
                    onClick = { showPasswordDialog = true }
                )
            }
            else {
                Box (Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                    contentAlignment = Alignment.Center
                    )
                {
                    CircularProgressIndicator()
                }
            }
        }
    }


    if (showDialog) {
        EditUserDialog(
            user = userProfile,
            onDismiss = { showDialog = false },
            onConfirm = { updatedUser ->
                userViewModel.updateProfile(
                    name =  updatedUser.name,
                    surname = updatedUser.surname,
                    email = updatedUser.email,
                    cookingHours = userProfile!!.cookingHours
                )
                showDialog = false
            }
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { oldPassword, newPassword  ->
//               TODO: userViewModel.updatePassword(oldPassword, newPassword)
                showPasswordDialog = false
            }
        )
    }

    LaunchedEffect(message) {
        if (message?.contains("Zmieniono", ignoreCase = true) == true) {
            showDialog = false
            Toast.makeText(context, "Zmieniono hasło pomyślnie", Toast.LENGTH_SHORT).show()
            userViewModel.succesfulMessage = null
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

}
