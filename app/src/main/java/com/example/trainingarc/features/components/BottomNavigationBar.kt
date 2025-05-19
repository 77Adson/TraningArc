package com.example.trainingarc.features.components

// features/components/BottomNavigationBar.kt

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.trainingarc.R
import com.example.trainingarc.navigation.Routes
import com.example.trainingarc.ui.theme.sizes


@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(Routes.Home, Routes.Friends, Routes.Settings, Routes.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = getIconRes(screen)),
                        contentDescription = screen.route,
                        modifier = Modifier
                            .size(MaterialTheme.sizes.icons.extraLarge)
                    )
                },
                selected = currentRoute == screen.route,
                onClick = { navigateToScreen(navController, screen) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.tertiary,
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                    // Indicator
                    indicatorColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.height(72.dp)
            )
        }
    }
}

@Composable
private fun getIconRes(screen: Routes): Int = when (screen) {
    Routes.Home -> R.drawable.ic_home  // Your actual drawable resource
    Routes.Friends -> R.drawable.ic_friends
    Routes.Settings -> R.drawable.ic_settings
    Routes.Profile -> R.drawable.ic_profile
    else -> R.drawable.ic_default  // Fallback icon
}

private fun navigateToScreen(navController: NavController, screen: Routes) {
    navController.navigate(screen.route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
    }
}