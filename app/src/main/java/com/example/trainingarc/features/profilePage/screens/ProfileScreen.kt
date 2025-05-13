package com.example.trainingarc.features.profilePage.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingarc.features.profilePage.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val userData by profileViewModel.userData
    val greeting by profileViewModel.greetingMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        userData?.email?.let { email ->
            Text(
                text = "Logged in as: $email",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Logout")
        }
    }
}