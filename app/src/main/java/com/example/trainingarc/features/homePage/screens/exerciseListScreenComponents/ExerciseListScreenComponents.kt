package com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trainingarc.features.homePage.model.Workout

@Composable
fun WorkoutListScreenContent(
    workouts: List<Workout>,
    onWorkoutClick: (String) -> Unit,
    onEditClick: (Workout) -> Unit,
    onDeleteClick: (Workout) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (workouts.isEmpty()) {
            EmptyWorkoutListPlaceholder()
        } else {
            WorkoutList(
                workouts = workouts,
                onWorkoutClick = onWorkoutClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
private fun EmptyWorkoutListPlaceholder() {
    Text(
        text = "No workouts yet",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun WorkoutList(
    workouts: List<Workout>,
    onWorkoutClick: (String) -> Unit,
    onEditClick: (Workout) -> Unit,
    onDeleteClick: (Workout) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        workouts.forEach { workout ->
            WorkoutListItem(
                workout = workout,
                onWorkoutClick = { onWorkoutClick(workout.id) },
                onEditClick = { onEditClick(workout) },
                onDeleteClick = { onDeleteClick(workout) }
            )
        }
    }
}

@Composable
fun WorkoutListItem(
    workout: Workout,
    onWorkoutClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        WorkoutListItemContent(
            name = workout.name,
            onWorkoutClick = onWorkoutClick,
            onDetailsClick = onWorkoutClick,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick
        )
    }
}

@Composable
private fun WorkoutListItemContent(
    name: String,
    onWorkoutClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(8.dp)
    ) {
        WorkoutName(
            name = name,
            onClick = onWorkoutClick,
            modifier = Modifier.weight(1f)
        )
        WorkoutActions(
            onDetailsClick = onWorkoutClick,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick
        )
    }
}

@Composable
private fun WorkoutName(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        modifier = modifier
            .padding(start = 8.dp)
            .clickable(onClick = onClick),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun WorkoutActions(
    onDetailsClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        IconButton(onClick = onDetailsClick) {
            Icon(Icons.Default.Details, "Details")
        }
        IconButton(onClick = onEditClick) {
            Icon(Icons.Default.Edit, "Edit")
        }
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, "Delete")
        }
    }
}