package com.example.trainingarc.features.homePage.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingarc.features.components.LoadingIndicator
import com.example.trainingarc.features.homePage.model.TrainingSession
import com.example.trainingarc.features.homePage.viewmodel.HomeViewModel
import com.example.trainingarc.navigation.Routes

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var newSessionName by remember { mutableStateOf("") }
    val sessions by homeViewModel.sessions
    val isLoading by homeViewModel.isLoading

    if (isLoading) {
        LoadingIndicator()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Your Training Sessions",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                icon = { Icon(Icons.Default.Add, "Add") },
                text = { Text("New Training Session") }
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sessions) { session ->
                    TrainingSessionCard(
                        session = session,
                        onClick = {
                            navController.navigate(Routes.WorkoutList.createRoute(session.sessionId))
                        }
                    )
                }
            }
        }
    }

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
}

@Composable
fun TrainingSessionCard(session: TrainingSession, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = session.sessionName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
