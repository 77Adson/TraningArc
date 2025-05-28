package com.example.trainingarc.features.homePage.screens

import androidx.compose.foundation.layout.padding
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
import com.example.trainingarc.features.homePage.model.Exercise
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.AddWorkoutDialog
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.DeleteSessionDialog
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.DeleteWorkoutDialog
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.EditWorkoutDialog
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.FloatingAddButton
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.WorkoutListScreenContent
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.WorkoutListTopBar
import com.example.trainingarc.features.homePage.viewmodel.ExercisesListViewModel
import com.example.trainingarc.navigation.Routes

@Composable
fun WorkoutListScreen(
    sessionId: String,
    navController: NavController,
    viewModel: ExercisesListViewModel = viewModel()
) {
    val exercises by viewModel.exercises.collectAsState()
    val currentSession by viewModel.currentSession.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var currentExercise by remember { mutableStateOf<Exercise?>(null) }
    var newExerciseName by remember { mutableStateOf("") }
    var showDeleteSessionConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(sessionId) {
        viewModel.getExercisesForSession(sessionId)
    }

    Scaffold(
        topBar = {
            WorkoutListTopBar(
                onBackClick = { navController.popBackStack() },
                onDeleteSession = { showDeleteSessionConfirm = true }
            )
        },
        floatingActionButton = {
            FloatingAddButton(
                onPrimaryClick = { /* Toggle visibility */ },
                onCreateClick = { showAddDialog = true },
                onAddExistingClick = { /* Handle existing */ }
            )
        }
    ) { innerPadding ->
        WorkoutListScreenContent(
            workouts = exercises, // Directly pass Exercise objects
            onWorkoutClick = { exerciseId ->
                navController.navigate(
                    Routes.WorkoutDetail.createRoute(
                        sessionId = sessionId,
                        exerciseId = exerciseId
                    )
                )
            },
            onEditClick = { exercise: Exercise -> // Now receives full Exercise
                currentExercise = exercise
                newExerciseName = exercise.exerciseName
                showEditDialog = true
            },
            onDeleteClick = { exercise: Exercise -> // Now receives full Exercise
                currentExercise = exercise
                showDeleteConfirm = true
            },
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (showAddDialog) {
        AddWorkoutDialog(
            name = newExerciseName,
            onNameChange = { newExerciseName = it },
            onConfirm = {
                viewModel.addExercise(
                    sessionId = sessionId,
                    exercise = Exercise(
                        exerciseName = newExerciseName,
                        weight = 0f,
                        reps = 0,
                        sets = 0,
                        description = ""
                    )
                )
                newExerciseName = ""
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    // Edit Workout Dialog
    if (showEditDialog && currentExercise != null) {
        EditWorkoutDialog(
            name = newExerciseName,
            onNameChange = { newExerciseName = it },
            onConfirm = {
                currentExercise?.let { exercise ->
                    viewModel.updateExercise(
                        exercise.copy(exerciseName = newExerciseName)
                    )
                }
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm && currentExercise != null) {
        DeleteWorkoutDialog(
            onConfirm = {
                currentExercise?.let { exercise ->
                    viewModel.deleteExercise(exercise.exerciseId)
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
                        // Handle error (show snackbar or log)
                        showDeleteSessionConfirm = false
                    }
                )
            },
            onDismiss = { showDeleteSessionConfirm = false }
        )
    }
}