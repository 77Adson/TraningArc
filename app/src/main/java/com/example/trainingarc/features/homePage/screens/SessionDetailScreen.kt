// features/homePage/screens/SessionDetailScreen.kt
package com.example.trainingarc.features.homePage.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.trainingarc.features.homePage.viewmodel.SessionViewModel

// features/homePage/screens/SessionDetailScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    sessionId: String?,
    navController: NavController = rememberNavController(),
    sessionViewModel: SessionViewModel = viewModel()
) {
    // Observe ViewModel state
    val sessionName by sessionViewModel.sessionName.collectAsState()
    val isLoading by sessionViewModel.isLoading.collectAsState()

    LaunchedEffect(sessionId) {
        sessionId?.let { sessionViewModel.loadSessionData(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text(sessionName.ifEmpty { "Unknown Session" })
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
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
    }
}