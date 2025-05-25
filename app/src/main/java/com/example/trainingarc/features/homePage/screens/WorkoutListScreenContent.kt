package com.example.trainingarc.features.homePage.screens.buttonsAndCards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.trainingarc.features.homePage.model.Workout

@Composable
fun WorkoutListScreenContent(
    workouts: List<Workout>,
    onWorkoutClick: (Workout) -> Unit,
    onEditClick: (Workout) -> Unit,
    onDeleteClick: (Workout) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (workouts.isEmpty()) {
            Text("Brak ćwiczeń", style = MaterialTheme.typography.bodyMedium)
        } else {
            workouts.forEach { workout ->
                WorkoutItem(
                    workout = workout,
                    onItemClick = { onWorkoutClick(workout) },
                    onEditClick = { onEditClick(workout) },
                    onDeleteClick = { onDeleteClick(workout) }
                )
                Divider()
            }
        }
    }
}

@Composable
private fun WorkoutItem(
    workout: Workout,
    onItemClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(workout.name) },
        trailingContent = {
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Usuń")
                }
            }
        },
        modifier = Modifier.clickable(onClick = onItemClick)
    )
}