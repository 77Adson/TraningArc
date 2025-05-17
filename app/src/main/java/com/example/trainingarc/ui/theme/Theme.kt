package com.example.trainingarc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MyrtleGreen,
    secondary = Isabelline,
    tertiary = Rust,
    background = Gunmetal,
    surface = Wenge,
    onPrimary = Isabelline,
    onSecondary = Isabelline,
    onTertiary = Isabelline,
    onBackground = Isabelline,
    onSurface = Isabelline
)

private val LightColorScheme = lightColorScheme(
    primary = Rust,
    secondary = Wenge,
    tertiary = Rust,
    background = Isabelline,
    surface = Isabelline,
    onPrimary = Gunmetal,
    onSecondary = Gunmetal,
    onTertiary = Gunmetal,
    onBackground = Gunmetal,
    onSurface = Gunmetal
)

@Composable
fun TrainingArcTheme(
    darkTheme: Boolean = true,
    // Keep disabled for now
    content: @Composable () -> Unit
) {
    // Always use our custom colors, ignoring dynamic colors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = AppShapes
    )
}

