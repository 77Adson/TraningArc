package com.example.trainingarc.features.homePage.screens.exerciseScreenComponents

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Save // Keep for default or specific cases
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.trainingarc.ui.theme.sizes // Assuming this is your custom theme sizes

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null, // Make icon optional
    colors: ButtonColors = ButtonDefaults.buttonColors() // Allow overriding colors
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp), // Consider making height configurable if needed
        shape = MaterialTheme.shapes.medium,
        colors = colors
    ) {
        icon?.let {
            Icon(imageVector = it, contentDescription = null)
            Spacer(modifier = Modifier.width(MaterialTheme.sizes.spacing.medium))
        }
        Text(text)
    }
}

// Your SaveButton can now use AppButton
@Composable
fun SaveButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppButton(
        text = "Save",
        onClick = onClick,
        enabled = enabled,
        icon = Icons.Default.Save,
        modifier = modifier
        // You could also pass specific colors here if needed
    )
}

// Example of another button using AppButton
@Composable
fun ShowGraphButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppButton(
        text = "Show Graph",
        onClick = onClick,
        icon = Icons.Default.AutoGraph,
        modifier = modifier,
        // colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary) // Example color override
    )
}