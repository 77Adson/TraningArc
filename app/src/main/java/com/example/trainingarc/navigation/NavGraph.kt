package com.example.trainingarc.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.trainingarc.features.homePage.model.ExerciseWithId
import com.example.trainingarc.features.homePage.screens.HomeScreen
import com.example.trainingarc.features.homePage.screens.ExerciseScreen
import com.example.trainingarc.features.homePage.screens.ExerciseListScreen
import com.example.trainingarc.features.homePage.screens.ProgressChartScreen
import com.example.trainingarc.features.homePage.viewmodel.ExercisesListViewModel
import com.example.trainingarc.features.profilePage.screens.ProfileScreen
import com.example.trainingarc.features.settingsPage.screen.SettingsScreen

@Composable
fun NavGraph(
    authViewModel: AuthViewModel = viewModel()
) {
    println("DEBUG 1: NavGraph composition started")
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Stateflow from ViewModel
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val showBottomBar = currentRoute in Routes.bottomNavRoutes && authState.isLoggedIn

    println("DEBUG 2: AuthState = isLoggedIn:${authState.isLoggedIn}, isLoading:${authState.isLoading}")
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        val startDest = if (authState.isLoggedIn) Routes.Home.route else Routes.Login.route
        println("DEBUG 3: Start destination = $startDest")
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth
            composable(Routes.Login.route) {
                println("DEBUG 4: Login screen")
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Routes.Register.route)
                    }
                )
            }

            composable(Routes.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
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
                println("Starting Home screen")
                HomeScreen(navController = navController)
            }

            composable(
                route = Routes.WorkoutList.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.WorkoutList.route)
                }
                val viewModel: ExercisesListViewModel = viewModel(parentEntry)

                LaunchedEffect(sessionId) {
                    viewModel.getExercisesForSession(sessionId)
                }

                ExerciseListScreen(
                    sessionId = sessionId,
                    navController = navController,
                    viewModel = viewModel
                )
            }

            composable(
                route = Routes.WorkoutDetail.route,
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.StringType },
                    navArgument("exerciseId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
                val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.WorkoutList.route)
                }
                val viewModel: ExercisesListViewModel = viewModel(parentEntry)

                ExerciseScreen(
                    sessionId = sessionId,
                    exerciseId = exerciseId,
                    viewModel = viewModel,
                    navController = navController
                )
            }

            composable("progressChart/{exerciseId}") { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.WorkoutList.route)
                }
                val viewModel: ExercisesListViewModel = viewModel(parentEntry)
                val exercises by viewModel.exercises.collectAsState()

                val exercise = exercises.find { it.id == exerciseId }

                if (exercise != null) {
                    ProgressChartScreen(exercise = exercise)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nie znaleziono ćwiczenia")
                    }
                }
            }
        }
    }
}