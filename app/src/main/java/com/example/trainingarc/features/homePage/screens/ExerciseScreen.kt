package com.example.trainingarc.features.homePage.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingarc.features.homePage.screens.exerciseScreenComponents.EditableNumberField
import com.example.trainingarc.features.homePage.viewmodel.ExerciseViewModel
import com.example.trainingarc.ui.theme.sizes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    sessionId: String,
    exerciseId: String,
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel()
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
        modifier = Modifier.fillMaxSize(),
//        contentWindowInsets = WindowInsets(0, 0, 0, 0), // This removes default padding
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(), // Add padding for status bar
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                        ){
                            var headlineStyle = MaterialTheme.typography.headlineMedium
                            Text(
                                text = exercise?.exercise?.exerciseName ?: "Exercise Details",
                                style = headlineStyle,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                onTextLayout = { textLayoutResult ->
                                    if (textLayoutResult.didOverflowWidth) {
                                        headlineStyle = headlineStyle.copy(
                                            fontSize = headlineStyle.fontSize * 0.5
                                        )
                                    }
                                },
                                )
                        }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    //remove exercise button
                    IconButton(
                        onClick = { showDeleteConfirm = true }
                    ){
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
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
                style = MaterialTheme.typography.titleLarge
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

            Button(
                onClick = {
                    exercise?.let {
                        viewModel.updateExerciseStats(
                            exerciseId = it.id,
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
                } ?: false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ){
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(MaterialTheme.sizes.spacing.medium))
                Text("Save")
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