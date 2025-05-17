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
import com.example.trainingarc.features.homePage.model.WorkoutDetail
import com.example.trainingarc.features.homePage.viewmodel.WorkoutDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    workoutId: String,
    navController: NavController,
    viewModel: WorkoutDetailViewModel = viewModel()
) {
    // Initialize with default empty WorkoutDetail
    val detailState by viewModel.detail.collectAsState(initial = WorkoutDetail(workoutId, ""))
    var description by remember { mutableStateOf(detailState?.description ?: "") }
    var isEditing by remember { mutableStateOf(false) }

    // Load details when screen opens or workoutId changes
    LaunchedEffect(workoutId) {
        viewModel.getDetail(workoutId)
    }

    // Update local description when detailState changes
    LaunchedEffect(detailState) {
        description = detailState?.description ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text(text = "Workout ID: $workoutId")

            Spacer(modifier = Modifier.height(16.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Button(onClick = {
                        viewModel.updateDescription(workoutId, description)
                        isEditing = false
                    }) {
                        Text("Save")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(onClick = {
                        description = detailState?.description ?: ""
                        isEditing = false
                    }) {
                        Text("Cancel")
                    }
                }
            } else {
                Text(text = "Description: ${detailState?.description ?: "No description"}")

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { isEditing = true }) {
                    Text("Edit Description")
                }
            }
        }
    }
}