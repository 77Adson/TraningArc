package com.example.trainingarc.features.auth.model

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: AuthError? = null
)