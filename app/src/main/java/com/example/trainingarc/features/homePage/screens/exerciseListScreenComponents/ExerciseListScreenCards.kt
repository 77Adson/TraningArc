package com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.trainingarc.features.homePage.model.Workout
import com.example.trainingarc.ui.theme.sizes

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
    var expandedWorkoutId by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier) {
        workouts.forEach { workout ->
            WorkoutListItem(
                workout = workout,
                isExpanded = workout.id == expandedWorkoutId,
                onWorkoutClick = { onWorkoutClick(workout.id) },
                onEditClick = { onEditClick(workout) },
                onDeleteClick = { onDeleteClick(workout) },
                onExpandChange = { shouldExpand ->
                    expandedWorkoutId = if (shouldExpand) workout.id else null
                }
            )
        }
    }
}

@Composable
fun WorkoutListItem(
    workout: Workout,
    isExpanded: Boolean,
    onWorkoutClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isExpanded)
    {
        MaterialTheme.colorScheme.primary
    }else{
        MaterialTheme.colorScheme.surface
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(MaterialTheme.sizes.components.smallCardHeight)
            .padding(vertical = 4.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onWorkoutClick() },
                    onLongPress = { onExpandChange(true) }
                )
            },

        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        WorkoutListItemContent(
            name = workout.name,
            showActions = isExpanded,
            onEditClick = { onExpandChange(false); onEditClick() },
            onDeleteClick = { onExpandChange(false); onDeleteClick() }
        )
    }
}

@Composable
private fun WorkoutListItemContent(
    name: String,
    showActions: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        WorkoutName(
            name = name,
            modifier = Modifier.weight(1f)
        )

        if (showActions) {
            WorkoutActions(
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
private fun WorkoutName(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        modifier = modifier
            .padding(start = 8.dp),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun WorkoutActions(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        IconButton(onClick = onEditClick) {
            Icon(Icons.Default.Edit, "Edit")
        }
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, "Delete")
        }
    }
}