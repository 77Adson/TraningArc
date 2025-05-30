package com.example.trainingarc.features.homePage.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingarc.features.homePage.screens.exerciseScreenComponents.EditableNumberField
import com.example.trainingarc.features.homePage.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    sessionId: String,
    exerciseId: String,
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel()  // Changed to ExerciseViewModel
) {
    // Load exercise details
    LaunchedEffect(exerciseId) {
        viewModel.getExerciseDetail(exerciseId)
    }

    val exercise by viewModel.detail.collectAsState()
    var localSets by remember { mutableIntStateOf(0) }
    var localReps by remember { mutableIntStateOf(0) }
    var localWeight by remember { mutableFloatStateOf(0f) }
    var localDescription by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Initialize local state when exercise loads
    LaunchedEffect(exercise) {
        exercise?.exercise?.let {
            localSets = it.sets
            localReps = it.reps
            localWeight = it.weight
            localDescription = it.description
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exercise?.exercise?.exerciseName ?: "Exercise Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Save button in top bar
                    IconButton(
                        onClick = {
                            exercise?.let {
                                viewModel.updateExerciseStats(
                                    exerciseId  = it.id,
                                    sets = localSets,
                                    reps = localReps,
                                    weight = localWeight
                                )
                            }
                        },
                        enabled = exercise?.let {
                            it.exercise.sets != localSets ||
                                    it.exercise.reps != localReps ||
                                    it.exercise.weight != localWeight
                        } ?: false
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Description field (always editable)
            OutlinedTextField(
                value = localDescription,
                onValueChange = { localDescription = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                trailingIcon = {
                    if (localDescription != exercise?.exercise?.description) {
                        IconButton(
                            onClick = {
                                exercise?.let {
                                    viewModel.updateDescription(it.id, localDescription)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Exercise stats
            Text(
                text = "Exercise Stats:",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            EditableNumberField(
                label = "Sets",
                value = localSets,
                onValueChange = { localSets = it.toInt() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            EditableNumberField(
                label = "Reps",
                value = localReps,
                onValueChange = { localReps = it.toInt() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            EditableNumberField(
                label = "Weight (kg)",
                value = localWeight,
                onValueChange = { localWeight = it.toFloat() },
                isFloat = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Delete button
            OutlinedButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Exercise")
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this exercise?") },
            confirmButton = {
                Button(
                    onClick = {
                        exercise?.let {
                            viewModel.deleteExercise(
                                exerciseId = it.id, // Assuming it.id is the correct ID for deletion
                                sessionId = sessionId,
                                onSuccess = {
                                    // This code will run AFTER successful deletion
                                    navController.popBackStack()
                                    showDeleteConfirm = false // Also dismiss the dialog
                                }
                            )
                        }
                        // If exercise is null, you might want to dismiss the dialog anyway
                        if (exercise == null) {
                            showDeleteConfirm = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}