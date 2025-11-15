package com.example.frontend.ui.screens


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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.firstcomposeap.ui.components.UniversalEditCard
import com.example.firstcomposeap.ui.components.profile.profileTab.EditUserDialog
import com.example.firstcomposeap.ui.navigation.main.MainLayout
import com.example.frontend.ui.service.LoginViewModel

@Composable
fun ProfileScreen(navController: NavHostController,
                  userViewModel: LoginViewModel
) {
    var selectedItem by remember { mutableStateOf("Konto") }
    userViewModel.downloadUserProfile()

    var userProfile = userViewModel.userProfile
    LaunchedEffect(userViewModel.userProfile) {
        userProfile = userViewModel.userProfile
    }
    var showDialog by remember { mutableStateOf(false) }

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
//                TODO: modyfikacja
                showDialog = false
            }
        )
    }
}
