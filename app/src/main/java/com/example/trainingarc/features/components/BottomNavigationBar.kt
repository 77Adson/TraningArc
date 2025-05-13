package com.example.trainingarc.features.components

// features/components/BottomNavigationBar.kt

import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.trainingarc.navigation.Routes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination


@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val items = listOf(
        Routes.Home,
        Routes.Friends,
        Routes.Settings,
        Routes.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    // Using colored circles as placeholders for icons
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (screen) {
                            Routes.Home -> Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color.Red, CircleShape)
                            )
                            Routes.Friends -> Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color.Blue, CircleShape)
                            )
                            Routes.Settings -> Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color.Green, CircleShape)
                            )
                            Routes.Profile -> Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color.Yellow, CircleShape)
                            )
                            else -> {}
                        }
                    }
                },
                label = { Text(screen.route) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting an item
                        restoreState = true
                        // Pop up to the start destination to avoid building a large stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.LightGray,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.LightGray
                )
            )
        }
    }
}