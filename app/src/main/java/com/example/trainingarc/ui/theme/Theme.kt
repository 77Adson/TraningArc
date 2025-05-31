package com.example.trainingarc.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
    onSurface = Isabelline,
    error = Rust,
    onError = Gunmetal
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

data class AppSizes(
    val spacing: Spacing = Spacing(),
    val icons: Icons = Icons(),
    val components: Components = Components()
) {
    data class Spacing(
        val none: Dp = 0.dp,
        val extraSmall: Dp = 4.dp,
        val small: Dp = 8.dp,
        val medium: Dp = 16.dp,
        val large: Dp = 24.dp,
        val extraLarge: Dp = 32.dp
    )

    data class Icons(
        val small: Dp = 16.dp,
        val medium: Dp = 24.dp,
        val large: Dp = 32.dp,
        val extraLarge: Dp = 40.dp
    )

    data class Components(
        val buttonHeight: Dp = 48.dp,
        val navBarHeight: Dp = 64.dp,
        val cardHeight: Dp = 80.dp,
        val smallCardHeight: Dp = 60.dp,
        val cardWidth: Dp = 160.dp,
        val largeCardWidth: Dp = 270.dp,
    )
}

val LocalSizes = staticCompositionLocalOf { AppSizes() }

// Add this to expose custom shapes
val LocalAppShapes = staticCompositionLocalOf { AppShapes }

val MaterialTheme.appShapes: Shapes
    @Composable
    @ReadOnlyComposable
    get() = LocalAppShapes.current

val MaterialTheme.sizes: AppSizes
    @Composable
    @ReadOnlyComposable
    get() = LocalSizes.current

@Composable
fun TrainingArcTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val appSizes = AppSizes()

    CompositionLocalProvider(
        LocalSizes provides appSizes
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
            shapes = AppShapes
        )
    }
}


