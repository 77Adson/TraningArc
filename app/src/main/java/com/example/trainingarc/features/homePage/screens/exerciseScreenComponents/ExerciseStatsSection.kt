package com.example.trainingarc.features.homePage.screens.exerciseScreenComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExerciseStatsSection(
    sets: Int,
    reps: Int,
    weight: Float,
    onSetsChange: (Number) -> Unit,
    onRepsChange: (Number) -> Unit,
    onWeightChange: (Number) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Exercise Stats:",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditableNumberField(
            label = "Sets",
            value = sets,
            onValueChange = onSetsChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditableNumberField(
            label = "Reps",
            value = reps,
            onValueChange = onRepsChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditableNumberField(
            label = "Weight (kg)",
            value = weight,
            onValueChange = onWeightChange,
            isFloat = true,
            delta = 2.5f
        )
    }
}