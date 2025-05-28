package com.example.trainingarc.navigation

// navigation/Routes.kt
sealed class Routes(val route: String) {

    // Auth navigation
    data object Login : Routes("login")
    data object Register : Routes("register")

    // Main app navigation (bottom panel)
    data object Home : Routes("home")
    data object Friends : Routes("friends")
    data object Settings : Routes("settings")
    data object Profile : Routes("profile")

    data object WorkoutList : Routes("workoutList/{sessionId}") {
        fun createRoute(sessionId: String): String = "workoutList/$sessionId"
    }

    data object WorkoutDetail : Routes("workoutDetail/{sessionId}/{exerciseId}") {
        fun createRoute(sessionId: String, exerciseId: String) =
            "workoutDetail/$sessionId/$exerciseId"
    }

    companion object {
        // Helper list of all bottom nav screens
        private val bottomNavItems = listOf(Home, Friends, Settings, Profile)

        // List of just the route strings for easy checking
        val bottomNavRoutes = bottomNavItems.map { it.route }
    }
}
