package com.example.trainingarc.features.homePage.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.trainingarc.features.homePage.screens.buttonsAndCards.AddWorkoutDialog
import com.example.trainingarc.features.homePage.screens.buttonsAndCards.DeleteSessionDialog
import com.example.trainingarc.features.homePage.screens.buttonsAndCards.DeleteWorkoutDialog
import com.example.trainingarc.features.homePage.screens.buttonsAndCards.EditWorkoutDialog
import com.example.trainingarc.features.homePage.screens.buttonsAndCards.WorkoutListScreenContent
import com.example.trainingarc.features.homePage.screens.buttonsAndCards.WorkoutListTopBar
import com.example.trainingarc.features.homePage.model.Workout
import com.example.trainingarc.features.homePage.viewmodel.ExercisesListViewModel
import com.example.trainingarc.navigation.Routes

@Composable
fun WorkoutListScreen(
    sessionId: String,
    navController: NavController,
    viewModel: ExercisesListViewModel = viewModel()
) {
    val workouts by viewModel.workouts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var currentWorkout by remember { mutableStateOf<Workout?>(null) }
    var newWorkoutName by remember { mutableStateOf("") }
    var showDeleteSessionConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(sessionId) {
        viewModel.setCurrentSession(sessionId)
        viewModel.getExercisesList(sessionId)
    }

    Scaffold(
        topBar = {
            WorkoutListTopBar(
                onBackClick = { navController.popBackStack() },
                onDeleteSession = { showDeleteSessionConfirm = true }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, "Add Workout")
            }
        }
    ) { innerPadding ->
        WorkoutListScreenContent(
            workouts = workouts,
            onWorkoutClick = { workoutId ->
                // Poprawione wywołanie navigate
                navController.navigate(
                    route = Routes.ExerciseDetail.createRoute(workoutId),
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            },
            onEditClick = { workout ->
                currentWorkout = workout
                newWorkoutName = workout.name
                showEditDialog = true
            },
            onDeleteClick = { workout ->
                currentWorkout = workout
                showDeleteConfirm = true
            },
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (showAddDialog) {
        AddWorkoutDialog(
            name = newWorkoutName,
            onNameChange = { newWorkoutName = it },
            onConfirm = {
                viewModel.addExercise(sessionId, newWorkoutName)
                newWorkoutName = ""
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    // Edit Workout Dialog
    if (showEditDialog && currentWorkout != null) {
        EditWorkoutDialog(
            name = newWorkoutName,
            onNameChange = { newWorkoutName = it },
            onConfirm = {
                currentWorkout?.let { workout ->
                    viewModel.updateExerciseName(sessionId, workout.id, newWorkoutName)
                }
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm && currentWorkout != null) {
        DeleteWorkoutDialog(
            onConfirm = {
                currentWorkout?.let { workout ->
                    viewModel.deleteExerciese(sessionId, workout.id)
                }
                showDeleteConfirm = false
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }

    if (showDeleteSessionConfirm) {
        DeleteSessionDialog(
            onConfirm = {
                viewModel.deleteCurrentSession(
                    onSuccess = {
                        showDeleteSessionConfirm = false
                        navController.popBackStack()
                    },
                    onFailure = { e ->
                        showDeleteSessionConfirm = false
                    }
                )
            },
            onDismiss = { showDeleteSessionConfirm = false }
        )
    }
}
