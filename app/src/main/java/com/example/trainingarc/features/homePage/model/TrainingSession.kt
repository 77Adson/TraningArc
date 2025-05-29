// TrainingData.kt
package com.example.trainingarc.features.homePage.model

data class TrainingSession(
//    val sessionId: String = "",
    val sessionName: String = "",
    val sessionExercises: Map<String, Int> = emptyMap() // exerciseId to order
)

data class Exercise(
//    val exerciseId: String = "",
    val exerciseName: String = "",
    val weight: Float = 0f,
    val reps: Int = 0,
    val sets: Int = 0,
    val description: String = ""
)

// Add this to your model file (TrainingData.kt)
data class ExerciseWithId(
    val id: String,
    val exercise: Exercise
) {
    // Convenience properties to access exercise fields directly
    val exerciseName get() = exercise.exerciseName
    val weight get() = exercise.weight
    val reps get() = exercise.reps
    val sets get() = exercise.sets
    val description get() = exercise.description
}