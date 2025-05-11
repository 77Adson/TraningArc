package com.example.trainingarc.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trainingarc.features.auth.screens.LoginScreen
import com.example.trainingarc.features.auth.screens.RegisterScreen
import com.example.trainingarc.features.auth.viewmodel.AuthViewModel
import com.example.trainingarc.features.home.screens.HomeScreen

@Composable
fun NavGraph(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (authViewModel.isLoggedIn.value) Routes.Home.route else Routes.Login.route
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                onLogin = { email, password ->
                    authViewModel.loginUser(email, password) { success ->
                        if (success) navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                onRegister = { email, password ->
                    authViewModel.registerUser(email, password) { success ->
                        if (success) {
                            navController.popBackStack()
                        }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.Home.route) {
            HomeScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}