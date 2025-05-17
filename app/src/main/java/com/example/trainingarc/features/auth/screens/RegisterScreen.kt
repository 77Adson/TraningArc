package com.example.trainingarc.features.auth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingarc.features.auth.viewmodel.AuthViewModel
import com.example.trainingarc.features.components.EmailTextField
import com.example.trainingarc.features.components.LoadingIndicator
import com.example.trainingarc.features.components.PasswordTextField

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val authState by viewModel.state.collectAsStateWithLifecycle()

    // Handle successful registration
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            onRegisterSuccess()
        }
    }

    // Show loading indicator if loading
    if (authState.isLoading) {
        LoadingIndicator()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        EmailTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            showPassword = showPassword,
            onToggleVisibility = { showPassword = !showPassword },
            modifier = Modifier.fillMaxWidth(),
            label = "Password"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            showPassword = showPassword,
            onToggleVisibility = { showPassword = !showPassword },
            modifier = Modifier.fillMaxWidth(),
            label = "Confirm Password"
        )

        authState.error?.let { error ->
            Text(
                text = error.toUserMessage(),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.registerUser(
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = email.isNotBlank() &&
                    password.isNotBlank() &&
                    confirmPassword.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("Already have an account? Login")
        }
    }
}