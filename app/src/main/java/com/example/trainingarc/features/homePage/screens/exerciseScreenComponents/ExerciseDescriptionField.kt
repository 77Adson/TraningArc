package com.example.trainingarc.features.homePage.screens.exerciseScreenComponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ExerciseDescriptionField(
    description: String,
    currentDescription: String?,
    onDescriptionChange: (String) -> Unit,
    onSaveDescription: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        label = { Text("Description") },
        modifier = modifier.fillMaxWidth(),
        minLines = 3,
        maxLines = 5,
        trailingIcon = {
            if (description != currentDescription) {
                IconButton(onClick = onSaveDescription) {
                    Icon(Icons.Default.Check, contentDescription = "Save")
                }
            }
        }
    )
}