// com/example/trainingarc/ui/theme/Sizes.kt

package com.example.trainingarc.ui.theme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp

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
        val navBarHeight: Dp = 64.dp
    )
}