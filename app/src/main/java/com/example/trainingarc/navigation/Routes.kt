package com.example.trainingarc.navigation

// navigation/Routes.kt
sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object Home : Routes("home")
}