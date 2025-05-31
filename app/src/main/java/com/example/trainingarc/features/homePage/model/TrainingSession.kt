// TrainingData.kt
package com.example.trainingarc.features.homePage.model

import com.google.firebase.Timestamp
import java.util.Date
import com.google.firebase.firestore.PropertyName

data class TrainingSession(
    val sessionName: String = "",
    val sessionExercises: Map<String, Int> = emptyMap() // exerciseId → order
)

data class Exercise(
    val exerciseName: String = "",
    val weight: Float = 0f,
    val reps: Int = 0,
    val sets: Int = 0,
    val description: String = "",
    val history: Map<String, Float> = emptyMap() // date (ISO format) → score (weight × reps × sets)
) {
    fun currentScore(): Float = weight * reps * sets
}

data class ExerciseWithId(
    val id: String,
    val exercise: Exercise
) {
    val exerciseName get() = exercise.exerciseName
    val weight get() = exercise.weight
    val reps get() = exercise.reps
    val sets get() = exercise.sets
    val description get() = exercise.description
    val history get() = exercise.history

    fun currentScore(): Float = exercise.currentScore()

    fun addHistoryEntry(date: Date = Date()): ExerciseWithId {
        val dateKey = date.toISOFormat() // np. "2023-11-15T12:00:00Z"
        val newHistory = exercise.history.toMutableMap().apply {
            put(dateKey, currentScore())
        }
        return copy(exercise = exercise.copy(history = newHistory))
    }
}

fun Date.toISOFormat(): String = Timestamp(this).toDate().toString() // Firebase Timestamp → String