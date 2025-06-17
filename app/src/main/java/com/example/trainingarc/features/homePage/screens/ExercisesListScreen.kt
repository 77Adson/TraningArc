package com.example.trainingarc.features.homePage.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import com.example.trainingarc.features.homePage.model.ExerciseWithId
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.AddExistingExercisesSheet
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.AddWorkoutDialog
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.DeleteSessionDialog
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.DeleteWorkoutDialog
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.EditWorkoutDialog
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.FloatingAddButton
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.WorkoutListScreenContent
import com.example.trainingarc.features.homePage.screens.exerciseListScreenComponents.WorkoutListTopBar
import com.example.trainingarc.features.homePage.viewmodel.ExercisesListViewModel
import com.example.trainingarc.navigation.Routes
import com.example.trainingarc.ui.theme.sizes
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ExerciseListScreen(
    sessionId: String,
    navController: NavController,
    viewModel: ExercisesListViewModel = viewModel()
) {
    val exercises by viewModel.exercises.collectAsState()
    val currentSession by viewModel.currentSession.collectAsState()

    var showExistingExercises by remember { mutableStateOf(false) }
    val allExercises by viewModel.allExercises.collectAsState()
    val currentUser = Firebase.auth.currentUser // Get current user

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var currentExercise by remember { mutableStateOf<ExerciseWithId?>(null) }
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
                onPrimaryClick = { /* existing */ },
                onCreateClick = { showAddDialog = true },
                onAddExistingClick = {
                    currentUser?.uid?.let { userId ->
                        viewModel.loadAllUserExercises(userId)
                        showExistingExercises = true
                    }
                }
            )
        }
    ) { innerPadding ->
        WorkoutListScreenContent(
            workouts = exercises,
            onWorkoutClick = { exerciseId ->
                navController.navigate(
                    Routes.WorkoutDetail.createRoute(
                        sessionId = sessionId,
                        exerciseId = exerciseId
                    )
                )
            },
            onEditClick = { exercise ->
                currentExercise = exercise
                newExerciseName = exercise.exerciseName
                showEditDialog = true
            },
            onDeleteClick = { exercise ->
                currentExercise = exercise
                showDeleteConfirm = true
            },
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = MaterialTheme.sizes.spacing.medium)
        )

        AddExistingExercisesSheet(
            show = showExistingExercises,
            exercises = allExercises,
            onDismiss = { showExistingExercises = false },
            onExerciseSelected = { exerciseId ->
                currentUser?.uid?.let { userId ->
                    viewModel.addExistingExerciseToSession(userId, sessionId, exerciseId)
                }
                showExistingExercises = false
            }
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

    if (showEditDialog && currentExercise != null) {
        EditWorkoutDialog(
            name = newExerciseName,
            onNameChange = { newExerciseName = it },
            onConfirm = {
                currentExercise?.let { exerciseWithId ->
                    viewModel.updateExercise(
                        exerciseId = exerciseWithId.id,
                        exercise = exerciseWithId.exercise.copy(exerciseName = newExerciseName)
                    )
                }
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }

    if (showDeleteConfirm && currentExercise != null) {
        DeleteWorkoutDialog(
            onConfirm = {
                currentExercise?.let { exercise ->
                    viewModel.deleteExercise(exercise.id)
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
                    sessionKey = sessionId,
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