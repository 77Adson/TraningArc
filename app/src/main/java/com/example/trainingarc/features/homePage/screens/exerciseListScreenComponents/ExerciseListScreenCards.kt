package com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.trainingarc.features.homePage.model.ExerciseWithId
import com.example.trainingarc.ui.theme.sizes

@Composable
fun ExerciseListScreenContent(
    exercises: List<ExerciseWithId>,
    onExerciseClick: (String) -> Unit,
    onEditClick: (ExerciseWithId) -> Unit,
    onDeleteClick: (ExerciseWithId) -> Unit,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (exercises.isEmpty()) {
            EmptyExerciseListPlaceholder()
        } else {
            ExerciseList(
                exercises = exercises,
                onExerciseClick = onExerciseClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onMoveUp = onMoveUp,
                onMoveDown = onMoveDown
            )
        }
    }
}

@Composable
private fun EmptyExerciseListPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No exercises yet",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun ExerciseList(
    exercises: List<ExerciseWithId>,
    onExerciseClick: (String) -> Unit,
    onEditClick: (ExerciseWithId) -> Unit,
    onDeleteClick: (ExerciseWithId) -> Unit,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedExerciseId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        exercises.forEachIndexed { index, exercise ->
            ExerciseCard(
                exercise = exercise,
                isExpanded = exercise.id == expandedExerciseId,
                isFirst = index == 0,
                isLast = index == exercises.size - 1,
                onClick = { onExerciseClick(exercise.id) },
                onEditClick = { onEditClick(exercise) },
                onDeleteClick = { onDeleteClick(exercise) },
                onMoveUp = { onMoveUp(exercise.id) },
                onMoveDown = { onMoveDown(exercise.id) },
                onExpandChange = { shouldExpand ->
                    expandedExerciseId = if (shouldExpand) exercise.id else null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCard(
    exercise: ExerciseWithId,
    isExpanded: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isExpanded) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onExpandChange(!isExpanded) }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header row with name and reorder buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Reorder buttons
                ReorderButtons(
                    isFirst = isFirst,
                    isLast = isLast,
                    onMoveUp = onMoveUp,
                    onMoveDown = onMoveDown,
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Exercise name and basic info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.exerciseName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    ExerciseStats(exercise = exercise)
                }
            }

            // Expanded content (actions)
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                ExerciseActions(
                    onEditClick = {
                        onExpandChange(false)
                        onEditClick()
                    },
                    onDeleteClick = {
                        onExpandChange(false)
                        onDeleteClick()
                    }
                )
            }
        }
    }
}

@Composable
private fun ReorderButtons(
    isFirst: Boolean,
    isLast: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onMoveUp,
            enabled = !isFirst,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Move up",
                tint = if (isFirst) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                else MaterialTheme.colorScheme.primary
            )
        }

        IconButton(
            onClick = onMoveDown,
            enabled = !isLast,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = "Move down",
                tint = if (isLast) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ExerciseStats(exercise: ExerciseWithId) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Sets
        StatPill(
            value = exercise.sets.toString(),
            label = "Sets",
            color = MaterialTheme.colorScheme.primaryContainer
        )

        // Reps
        StatPill(
            value = exercise.reps.toString(),
            label = "Reps",
            color = MaterialTheme.colorScheme.secondaryContainer
        )

        // Weight
        if (exercise.weight > 0) {
            StatPill(
                value = "${exercise.weight} kg",
                label = "Weight",
                color = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
    }
}

@Composable
private fun StatPill(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun ExerciseActions(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Edit button
        FilledTonalButton(
            onClick = onEditClick,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit")
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Delete button
        FilledTonalButton(
            onClick = onDeleteClick,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Delete")
        }
    }
}