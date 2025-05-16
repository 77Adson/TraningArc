package com.example.trainingarc.features.homePage.model

data class TrainingSession(
    val sessionId: String = "",
    val sessionName: String = ""
)

data class Workout(
    val id: String = "",
    val name: String = ""
)

data class WorkoutDetail(
    val workoutId: String = "",
    val description: String = ""
)
