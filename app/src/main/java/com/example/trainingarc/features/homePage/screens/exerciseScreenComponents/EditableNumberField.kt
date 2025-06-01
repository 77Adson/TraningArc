package com.example.trainingarc.features.homePage.screens.exerciseScreenComponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.trainingarc.ui.theme.AppShapes
import com.example.trainingarc.ui.theme.pill
import com.example.trainingarc.ui.theme.sizes

// Reusable EditableNumberField component
@Composable
fun EditableNumberField(
    label: String,
    value: Number,
    onValueChange: (Number) -> Unit,
    modifier: Modifier = Modifier,
    isFloat: Boolean = false,
    delta: Float = 1f
) {
    var textValue by remember { mutableStateOf(value.toString()) }

    Column(
        modifier = modifier
            .padding(horizontal = MaterialTheme.sizes.spacing.medium, vertical = MaterialTheme.sizes.spacing.small)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizes.spacing.small)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .clip(AppShapes.pill)
                .background(MaterialTheme.colorScheme.surface)
                .height(MaterialTheme.sizes.components.cardHeight)
                .fillMaxWidth()
                .border( // Then apply the border
                    BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.onBackground),
                    shape = AppShapes.pill,
                )
        ) {
            IconButton(
                onClick = {
                    if (isFloat) {
                        val newValue = (value.toFloat() - delta).coerceAtLeast(0f)
                        onValueChange(newValue)
                    } else {
                        val newValue = (value.toInt() - delta.toInt()).coerceAtLeast(0)
                        onValueChange(newValue)
                    }
                }
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(MaterialTheme.sizes.icons.large)
                    )
            }

            OutlinedTextField(
                value = textValue,
                onValueChange = { newText ->
                    textValue = newText
                    if (newText.isNotEmpty()) {
                        try {
                            if (isFloat) {
                                val floatValue = newText.toFloat()
                                if (floatValue >= 0f) {
                                    onValueChange(floatValue)
                                }
                            } else {
                                val intValue = newText.toInt()
                                if (intValue >= 0) {
                                    onValueChange(intValue)
                                }
                            }
                        } catch (e: NumberFormatException) {
                            // Keep current value
                        }
                    }
                },
                modifier = Modifier
                    .height(50.dp)
                    .width(80.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isFloat) KeyboardType.Number else KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                colors = TextFieldDefaults.colors(
                    //container color
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    //border color
                    focusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0f),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0f),
                    //leading icon color
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                    //text color
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )

            IconButton(
                onClick = {
                    if (isFloat) {
                        onValueChange(value.toFloat() + delta)
                    } else {
                        onValueChange(value.toInt() + delta.toInt())
                    }
                }
            ) {
                Icon(Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(MaterialTheme.sizes.icons.large)
                )
            }
        }
    }

    // Update text field when value changes from outside
    LaunchedEffect(value) {
        textValue = value.toString()
    }
}