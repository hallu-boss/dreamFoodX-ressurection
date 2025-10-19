package com.example.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.frontend.ui.screens.LoginScreen
import com.example.frontend.ui.screens.RegisterScreen
import com.example.frontend.ui.theme.DreamFoodAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DreamFoodAppTheme {
                RegisterScreen()
            }
        }
    }
}