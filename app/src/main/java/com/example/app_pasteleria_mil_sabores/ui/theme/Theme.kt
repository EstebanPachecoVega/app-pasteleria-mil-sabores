package com.example.app_pasteleria_mil_sabores.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8B4513),        // chocolate
    onPrimary = Color(0xFFFFFFFF),      // blanco
    primaryContainer = Color(0xFFFFC0CB), // rosa
    onPrimaryContainer = Color(0xFF5D4037), // marrón texto

    secondary = Color(0xFF8B4513),      // chocolate
    onSecondary = Color(0xFFFFFFFF),    // blanco
    secondaryContainer = Color(0xFFFFC0CB), // rosa
    onSecondaryContainer = Color(0xFF5D4037), // marrón texto

    surface = Color(0xFFFFF5E1),        // crema
    onSurface = Color(0xFF5D4037),      // marrón texto
    background = Color(0xFFFFF5E1),     // crema
    onBackground = Color(0xFF5D4037),   // marrón texto
)

@Composable
fun AppPasteleriaMilSaboresTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = PasteleriaTypography,
        content = content
    )
}