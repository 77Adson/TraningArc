package com.example.trainingarc.features.homePage.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingarc.features.homePage.viewmodel.WorkoutViewModel
import com.example.trainingarc.navigation.Routes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutListScreen(
    sessionId: String,
    navController: NavController,
    viewModel: WorkoutViewModel = viewModel()
) {
    val workouts by viewModel.getWorkouts(sessionId).collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var newWorkoutName by remember { mutableStateOf("") }

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
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding).padding(16.dp)) {
            Button(onClick = { showDialog = true }) {
                Text("Add Workout")
            }

            Spacer(modifier = Modifier.height(16.dp))

            workouts.forEach { workout ->
                TextButton(onClick = {
                    navController.navigate(Routes.WorkoutDetail.createRoute(workout.id))
                }) {
                    Text(text = workout.name)
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("New Workout") },
                text = {
                    OutlinedTextField(
                        value = newWorkoutName,
                        onValueChange = { newWorkoutName = it },
                        label = { Text("Workout Name") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.addWorkout(sessionId, newWorkoutName)
                            showDialog = false
                            newWorkoutName = ""
                        },
                        enabled = newWorkoutName.isNotBlank()
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
