package com.example.trainingarc.features.homePage.screens.exerciseScreenComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Reusable EditableNumberField component
@Composable
fun EditableNumberField(
    label: String,
    value: Number,
    onValueChange: (Number) -> Unit,
    modifier: Modifier = Modifier,
    isFloat: Boolean = false
) {
    var textValue by remember { mutableStateOf(value.toString()) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (isFloat) {
                        val newValue = (value.toFloat() - 1f).coerceAtLeast(0f)
                        onValueChange(newValue)
                    } else {
                        val newValue = (value.toInt() - 1).coerceAtLeast(0)
                        onValueChange(newValue)
                    }
                }
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
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
                modifier = Modifier.width(80.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isFloat) KeyboardType.Number else KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true
            )

            IconButton(
                onClick = {
                    if (isFloat) {
                        onValueChange(value.toFloat() + 1f)
                    } else {
                        onValueChange(value.toInt() + 1)
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }
    }

    // Update text field when value changes from outside
    LaunchedEffect(value) {
        textValue = value.toString()
    }
}