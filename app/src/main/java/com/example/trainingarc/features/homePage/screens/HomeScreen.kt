package com.example.trainingarc.features.homePage.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingarc.features.components.LoadingIndicator
import com.example.trainingarc.features.homePage.screens.homeScreenComponents.TrainingSessionCard
import com.example.trainingarc.features.homePage.model.TrainingSession
import com.example.trainingarc.features.homePage.viewmodel.HomeViewModel
import com.example.trainingarc.navigation.Routes
import com.example.trainingarc.features.homePage.screens.homeScreenComponents.AddSessionButton

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var newSessionName by remember { mutableStateOf("") }
    var currentSession by remember { mutableStateOf<TrainingSession?>(null) }
    val sessions by homeViewModel.sessions.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.loadSessions()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background // Set the background color
    ) { innerPadding ->
        //Main content
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Your Training Sessions",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                LoadingIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(sessions) { session ->
                        TrainingSessionCard(
                            session = session,
                            onClick = {
                                navController.navigate(Routes.WorkoutList.createRoute(session.sessionId))
                            }
                        )
                    }
                    item {
                        AddSessionButton(
                            modifier = Modifier,
                            onClick = { showDialog = true }
                        )
                    }
                }
            }
        }

        // Dialog tworzenia nowej sesji
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("New Training Session") },
                text = {
                    OutlinedTextField(
                        value = newSessionName,
                        onValueChange = { newSessionName = it },
                        label = { Text("Session Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            homeViewModel.createNewSession(newSessionName) { success ->
                                if (success) {
                                    showDialog = false
                                    newSessionName = ""
                                }
                            }
                        },
                        enabled = newSessionName.isNotBlank()
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Dialog potwierdzenia usunięcia
        if (showDeleteDialog && currentSession != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirm Delete") },
                text = { Text("Are you sure you want to delete this session?") },
                confirmButton = {
                    Button(
                        onClick = {
                            currentSession?.let { session ->
                                homeViewModel.deleteSession(session.sessionId) {
                                    showDeleteDialog = false
                                }
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
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}