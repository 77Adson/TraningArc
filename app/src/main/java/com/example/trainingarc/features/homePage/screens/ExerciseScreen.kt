package com.example.trainingarc.features.homePage.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingarc.features.homePage.screens.exerciseScreenComponents.DeleteDialog
import com.example.trainingarc.features.homePage.screens.exerciseScreenComponents.ExerciseDescriptionField
import com.example.trainingarc.features.homePage.screens.exerciseScreenComponents.ExerciseStatsSection
import com.example.trainingarc.features.homePage.screens.exerciseScreenComponents.ExerciseTopBar
import com.example.trainingarc.features.homePage.screens.exerciseScreenComponents.SaveButton
import com.example.trainingarc.features.homePage.screens.exerciseScreenComponents.ShowGraphButton
import com.example.trainingarc.features.homePage.viewmodel.ExerciseViewModel
import com.example.trainingarc.navigation.Routes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.derivedStateOf

@Composable
fun ExerciseScreen(
    sessionId: String,
    exerciseId: String,
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel()
) {
    // State management
    LaunchedEffect(exerciseId) { viewModel.getExerciseDetail(exerciseId) }
    val exercise by viewModel.detail.collectAsState()

    var localSets by remember { mutableIntStateOf(0) }
    var localReps by remember { mutableIntStateOf(0) }
    var localWeight by remember { mutableFloatStateOf(0f) }
    var localDescription by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Check if there's history for today
    val hasHistoryForToday by remember(exercise) {
        derivedStateOf {
            val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            exercise?.exercise?.history?.containsKey(dateKey) ?: false
        }
    }

    // Initialize state
    LaunchedEffect(exercise) {
        exercise?.exercise?.let {
            localSets = it.sets
            localReps = it.reps
            localWeight = it.weight
            localDescription = it.description
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ExerciseTopBar(
                exerciseName = exercise?.exercise?.exerciseName,
                onBackClick = { navController.popBackStack() },
                onDeleteClick = { showDeleteConfirm = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            ExerciseDescriptionField(
                description = localDescription,
                currentDescription = exercise?.exercise?.description,
                onDescriptionChange = { localDescription = it },
                onSaveDescription = {
                    exercise?.let {
                        viewModel.updateDescription(it.id, localDescription)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ExerciseStatsSection(
                sets = localSets,
                reps = localReps,
                weight = localWeight,
                onSetsChange = { localSets = it as Int },
                onRepsChange = { localReps = it as Int },
                onWeightChange = { localWeight = it as Float }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SaveButton(
                enabled = exercise?.let {
                    it.exercise.sets != localSets ||
                    it.exercise.reps != localReps ||
                    it.exercise.weight != localWeight ||
                    !hasHistoryForToday
                } ?: false,
                onClick = {
                    exercise?.let {
                        viewModel.updateExerciseStats(
                            exerciseId = it.id,
                            sets = localSets,
                            reps = localReps,
                            weight = localWeight
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ShowGraphButton(
                onClick = {
                    exercise?.let {
                        navController.navigate(Routes.ProgressChart.createRoute(it.id))
                    }
                }
            )
        }
    }

    DeleteDialog(
        showDialog = showDeleteConfirm,
        onDismiss = { showDeleteConfirm = false },
        onConfirm = {
            exercise?.let {
                viewModel.deleteExercise(
                    exerciseId = it.id,
                    sessionId = sessionId,
                    onSuccess = {
                        navController.popBackStack()
                        showDeleteConfirm = false
                    }
                )
            }
        }
    )
}