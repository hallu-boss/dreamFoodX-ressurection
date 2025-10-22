package com.example.frontend.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val DreamFoodColorScheme = lightColorScheme(
    primary = greenDark,             // główny kolor akcji (przyciski)
    onPrimary = Color.White,         // tło napisów w przyciskach
    secondary = greenDark,                // kolor akcentowy
    onSecondary = Color.White,       // tekst/ikony na secondary
    background = backgroundDark,    // główne tło aplikacji
    onBackground = Color.White,      // tekst na tle background
    surface = backgroundDark,           // tło kart, paneli, TextFieldów
    onSurface = Color.Black,         // tekst na surface
    error = orange,                  // kolor błędów
    onError = Color.White            // tekst na błędach
)





@Composable
fun DreamFoodAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DreamFoodColorScheme
        else -> DreamFoodColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}