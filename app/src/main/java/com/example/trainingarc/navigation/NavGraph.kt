package com.example.trainingarc.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.trainingarc.features.auth.screens.LoginScreen
import com.example.trainingarc.features.auth.screens.RegisterScreen
import com.example.trainingarc.features.auth.viewmodel.AuthViewModel
import com.example.trainingarc.features.components.BottomNavigationBar
import com.example.trainingarc.features.friendsPage.screens.FriendsScreen
import com.example.trainingarc.features.homePage.screens.HomeScreen
import com.example.trainingarc.features.homePage.screens.SessionDetailScreen
import com.example.trainingarc.features.homePage.screens.WorkoutDetailScreen
import com.example.trainingarc.features.homePage.screens.WorkoutListScreen
import com.example.trainingarc.features.profilePage.screens.ProfileScreen
import com.example.trainingarc.features.settingsPage.screen.SettingsScreen

@Composable
fun NavGraph(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in Routes.bottomNavRoutes && authViewModel.isLoggedIn.value

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (authViewModel.isLoggedIn.value) Routes.Home.route else Routes.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth
            composable(Routes.Login.route) {
                LoginScreen(
                    onLogin = { email, password ->
                        authViewModel.loginUser(email, password) { success ->
                            if (success) {
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(Routes.Login.route) { inclusive = true }
                                }
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
                            if (success) navController.popBackStack()
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            // Main
            composable(Routes.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Home.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.Friends.route) { FriendsScreen() }
            composable(Routes.Settings.route) { SettingsScreen() }

            composable(Routes.Home.route) {
                HomeScreen(navController = navController) // ⬅️ przekazujemy właściwego
            }

            composable(
                route = Routes.SessionDetail.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId")
                SessionDetailScreen(
                    sessionId = sessionId,
                    navController = navController
                )
            }

            composable(
                route = Routes.WorkoutList.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
                WorkoutListScreen(sessionId = sessionId, navController = navController)
            }

            composable(
                route = Routes.WorkoutDetail.route,
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId") ?: return@composable
                WorkoutDetailScreen(workoutId = workoutId, navController = navController)
            }
        }
    }
}
