// TrainingData.kt
package com.example.trainingarc.features.homePage.model

data class TrainingSession(
//    val sessionId: String = "",
    val sessionName: String = "",
    val sessionExercises: Map<String, Int> = emptyMap() // exerciseId to order
)

data class Exercise(
    val exerciseId: String = "",
    val exerciseName: String = "",
    val weight: Float = 0f,
    val reps: Int = 0,
    val sets: Int = 0,
    val description: String = ""
)