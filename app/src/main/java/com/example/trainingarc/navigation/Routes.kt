package com.example.trainingarc.navigation

sealed class Routes(val route: String) {
    // Auth navigation
    data object Login : Routes("login")
    data object Register : Routes("register")

    // Main app navigation (bottom panel)
    data object Home : Routes("home")
    data object Friends : Routes("friends")
    data object Settings : Routes("settings")
    data object Profile : Routes("profile")

    data object SessionDetail : Routes("sessionDetail/{sessionId}") {
        fun createRoute(sessionId: String) = "sessionDetail/$sessionId"
    }

    data object WorkoutList : Routes("workoutList/{sessionId}") {
        fun createRoute(sessionId: String) = "workoutList/$sessionId"
    }

    data object ExerciseDetail : Routes("exerciseDetail/{sessionId}/{exerciseId}") {
        fun createRoute(sessionId: String, exerciseId: String) =
            "exerciseDetail/$sessionId/$exerciseId"
    }

    data object ProgressChart : Routes("progressChart/{exerciseId}") {
        fun createRoute(exerciseId: String) = "progressChart/$exerciseId"
    }

    companion object {
        // Helper list of all bottom nav screens
        private val bottomNavItems = listOf(Home, Friends, Settings, Profile)
        val bottomNavRoutes = bottomNavItems.map { it.route }
    }
}