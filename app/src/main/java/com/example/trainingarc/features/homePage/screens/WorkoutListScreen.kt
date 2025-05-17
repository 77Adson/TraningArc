package com.example.trainingarc.features.homePage.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingarc.features.homePage.model.Workout
import com.example.trainingarc.features.homePage.viewmodel.WorkoutViewModel
import com.example.trainingarc.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutListScreen(
    sessionId: String,
    navController: NavController,
    viewModel: WorkoutViewModel = viewModel()
) {
    val workouts by viewModel.workouts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var currentWorkout by remember { mutableStateOf<Workout?>(null) }
    var newWorkoutName by remember { mutableStateOf("") }

    LaunchedEffect(sessionId) {
        viewModel.getWorkouts(sessionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workouts") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    newWorkoutName = ""
                    showAddDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Workout")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (workouts.isEmpty()) {
                Text(
                    text = "No workouts yet",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                workouts.forEach { workout ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = workout.name,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp),
                                style = MaterialTheme.typography.titleMedium
                            )

                            IconButton(
                                onClick = {
                                    navController.navigate(Routes.WorkoutDetail.createRoute(workout.id))
                                }
                            ) {
                                Icon(Icons.Default.Info, "Details")
                            }

                            IconButton(
                                onClick = {
                                    currentWorkout = workout
                                    newWorkoutName = workout.name
                                    showEditDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Edit, "Edit")
                            }

                            IconButton(
                                onClick = {
                                    currentWorkout = workout
                                    showDeleteConfirm = true
                                }
                            ) {
                                Icon(Icons.Default.Delete, "Delete")
                            }
                        }
                    }
                }
            }
        }

        // Add Workout Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Workout") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newWorkoutName,
                            onValueChange = { newWorkoutName = it },
                            label = { Text("Workout Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.addWorkout(sessionId, newWorkoutName)
                            newWorkoutName = ""
                            showAddDialog = false
                        },
                        enabled = newWorkoutName.isNotBlank()
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Edit Workout Dialog
        if (showEditDialog && currentWorkout != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Edit Workout") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newWorkoutName,
                            onValueChange = { newWorkoutName = it },
                            label = { Text("Workout Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            currentWorkout?.let {
                                viewModel.updateWorkoutName(sessionId, it.id, newWorkoutName)
                            }
                            showEditDialog = false
                        },
                        enabled = newWorkoutName.isNotBlank()
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirm && currentWorkout != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Confirm Delete") },
                text = { Text("Are you sure you want to delete this workout?") },
                confirmButton = {
                    Button(
                        onClick = {
                            currentWorkout?.let {
                                viewModel.deleteWorkout(sessionId, it.id)
                            }
                            showDeleteConfirm = false
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