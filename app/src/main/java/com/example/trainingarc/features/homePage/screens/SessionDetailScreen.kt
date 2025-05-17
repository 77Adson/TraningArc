// features/homePage/screens/SessionDetailScreen.kt
package com.example.trainingarc.features.homePage.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.trainingarc.features.homePage.viewmodel.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    sessionId: String?,
    navController: NavController,
    sessionViewModel: SessionViewModel = viewModel()
) {
    val sessionName by sessionViewModel.sessionName.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var newSessionName by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    LaunchedEffect(sessionId) {
        sessionId?.let { sessionViewModel.loadSessionData(it) }
    }


    LaunchedEffect(Unit) {
        snapshotFlow { sessionViewModel.sessionName.value }
            .collect { newSessionName = it }
    }

    fun handleSessionDeleted() {
        navController.popBackStack()
        // Możesz też dodać snackbar na poprzednim ekranie:
        // navController.previousBackStackEntry?.savedStateHandle?.set("showSnackbar", true)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = sessionName.ifEmpty { "Session Details" },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showEditDialog = true },
                        enabled = sessionId != null
                    ) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = sessionId != null
                    ) {
                        Icon(Icons.Default.Delete, "Delete")
                    }
                }
            )
        },
        snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(8.dp),
                    action = {
                        TextButton(
                            onClick = { showSnackbar = false }
                        ) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(snackbarMessage)
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Exercises will be added here")
        }

        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Edit Session Name") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newSessionName,
                            onValueChange = { newSessionName = it },
                            label = { Text("Session Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            sessionId?.let {
                                sessionViewModel.updateSessionName(it, newSessionName)
                                showEditDialog = false
                                snackbarMessage = "Session updated successfully"
                                showSnackbar = true
                            }
                        },
                        enabled = newSessionName.isNotBlank()
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

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirm Deletion") },
                text = {
                    Text("Are you sure you want to delete this session? This action cannot be undone.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            sessionId?.let {
                                sessionViewModel.deleteSession(it) {
                                    handleSessionDeleted()
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