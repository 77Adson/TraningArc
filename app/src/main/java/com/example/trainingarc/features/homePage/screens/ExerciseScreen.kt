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
import com.example.trainingarc.features.homePage.viewmodel.ExercisesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    sessionId: String,
    exerciseId: String,
    navController: NavController,
    viewModel: ExercisesListViewModel = viewModel()
) {
    // Initialize with the session ID
    LaunchedEffect(sessionId) {
        viewModel.getExercisesForSession(sessionId)
    }

    val exercises by viewModel.exercises.collectAsState()
    val currentExercise = remember(exercises, exerciseId) {
        exercises.find { it.id == exerciseId }
    }

    var description by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Update description when exercise changes
    LaunchedEffect(currentExercise) {
        description = currentExercise?.exercise?.description ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentExercise?.exercise?.exerciseName ?: "Workout Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Button(
                        onClick = {
                            currentExercise?.let { exercise ->
                                viewModel.updateExercise(
                                    exerciseId = exercise.id,
                                    exercise = exercise.exercise.copy(description = description)
                                )
                            }
                            isEditing = false
                        }
                    ) {
                        Text("Save Changes")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = {
                            description = currentExercise?.exercise?.description ?: ""
                            isEditing = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            } else {
                Text(
                    text = "Description:",
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = currentExercise?.exercise?.description ?: "No description provided",
                    style = MaterialTheme.typography.bodyMedium
                )

                currentExercise?.exercise?.let { exercise ->
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Details:",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Sets: ${exercise.sets}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Reps: ${exercise.reps}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Weight: ${exercise.weight}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Description")
                }

                Spacer(modifier = Modifier.height(8.dp))

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

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Confirm Delete") },
                text = { Text("Are you sure you want to delete this exercise?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteExercise(exerciseId)
                            navController.popBackStack()
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
}