package com.example.temp_miau.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MiauPeachPrimary,
    secondary = MiauLavenderSecondary,
    tertiary = MiauMintTertiary,
    background = Color(0xFF181515),
    surface = Color(0xFF262020),
    surfaceVariant = Color(0xFF332B2B),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF7ECE1),
    onSurface = Color(0xFFF7ECE1)
)

private val LightColorScheme = lightColorScheme(
    primary = MiauPeachPrimary,
    secondary = MiauLavenderSecondary,
    tertiary = MiauMintTertiary,
    background = MiauBackgroundLight,
    surface = MiauSurfaceLight,
    surfaceVariant = MiauCardSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = MiauTextPrimary,
    onSurface = MiauTextPrimary
)

@Composable
fun Temp_miauTheme(
    darkTheme: Boolean = false, // Tema pastel cálido por defecto para la estética felina
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}