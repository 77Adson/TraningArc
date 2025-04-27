package com.example.trainingarc

// MainActivity.kt
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingarc.auth.AuthViewModel
import com.example.trainingarc.auth.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val authViewModel: AuthViewModel = viewModel()
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = { /* Switch to LoginScreen later */ },
                    onRegisterSuccess = { /* Navigate to HomeScreen later */ }
                )
            }
        }
    }
}