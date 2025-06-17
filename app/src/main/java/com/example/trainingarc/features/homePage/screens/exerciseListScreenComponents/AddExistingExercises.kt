package com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trainingarc.features.homePage.model.ExerciseWithId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExistingExercisesSheet(
    show: Boolean,
    exercises: List<ExerciseWithId>,
    onDismiss: () -> Unit,
    onExerciseSelected: (String) -> Unit
) {
    if (show) {
        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(exercises) { exercise ->
                    Button(
                        onClick = { onExerciseSelected(exercise.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(exercise.exerciseName)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ExerciseSelectionItem(
    exercise: ExerciseWithId,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onSelect,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = exercise.exerciseName)
    }
}