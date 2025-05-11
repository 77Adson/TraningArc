package com.example.trainingarc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingarc.auth.AuthScreen
import com.example.trainingarc.auth.AuthViewModel
import com.example.trainingarc.auth.LoginScreen
import com.example.trainingarc.auth.RegisterScreen
import com.example.trainingarc.ui.theme.TrainingArcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrainingArcTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthApp()
                }
            }
        }
    }
}

@Composable
fun AuthApp() {
    val authViewModel: AuthViewModel = viewModel()
    var showLogin by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var registerError by remember { mutableStateOf<String?>(null) }

    if (authViewModel.isLoggedIn.value) {
        AuthScreen(onLogout = {
            authViewModel.logout()
            showLogin = true
            isLoading = false
            loginError = null
            registerError = null
        })
    } else {
        if (showLogin) {
            LoginScreen(
                onLogin = { email, password ->
                    isLoading = true
                    loginError = null
                    authViewModel.loginUser(email, password) { success, message ->
                        isLoading = false
                        if (!success) {
                            loginError = message
                        }
                    }
                },
                onNavigateToRegister = {
                    showLogin = false
                    loginError = null
                },
                errorMessage = loginError,
                isLoading = isLoading,
                onErrorDismiss = { loginError = null }
            )
        } else {
            RegisterScreen(
                onRegister = { email, password ->
                    isLoading = true
                    registerError = null
                    authViewModel.registerUser(email, password) { success, message ->
                        isLoading = false
                        if (success) {
                            showLogin = true
                        } else {
                            registerError = message ?: "Unknown error"
                        }
                    }
                },
                onNavigateToLogin = {
                    showLogin = true
                    registerError = null
                },
                errorMessage = registerError,
                isLoading = isLoading,
                onErrorDismiss = { registerError = null }
            )
        }
    }
}