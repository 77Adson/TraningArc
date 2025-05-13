package com.example.trainingarc.navigation

// navigation/Routes.kt
sealed class Routes(val route: String) {

    // Auth navigation
    object Login : Routes("login")
    object Register : Routes("register")

    // Main app navigation (bottom panel)
    object Home : Routes("home")        //home screen
    object Friends : Routes("friends")  //firends screen
    object Settings : Routes("settings")  //settings screen
    object Profile : Routes("profile")  //profile screen

    // Subpages example
    object Training : Routes("training")

    companion object {
        // Helper list of all bottom nav screens
        val bottomNavItems = listOf(Home, Friends, Settings, Profile)

        // List of just the route strings for easy checking
        val bottomNavRoutes = bottomNavItems.map { it.route }
    }
}