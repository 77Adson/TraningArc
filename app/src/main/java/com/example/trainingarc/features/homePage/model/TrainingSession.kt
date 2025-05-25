package com.example.trainingarc.features.homePage.model

data class TrainingSession(
    val sessionId: String = "",
    val sessionName: String = ""
)

data class Workout(
    val id: String = "",
    val name: String = ""
)

data class ExerciseDetail(
    val workoutId: String = "",
    val name: String = "",
    val weight: Double = 0.0,
    val reps: Int = 0,
    val sets: Int = 0,
    val notes: String? = null,
    val history: Map<String, ExerciseHistoryEntry> = emptyMap()
) {
    fun getHistoryList(): List<ExerciseHistoryEntry> = history.values.toList()
}

data class ExerciseHistoryEntry(
    val timestamp: Long = 0L,
    val weight: Double,
    val reps: Int,
    val sets: Int,
    val notes: String? = null
) {
    val score: Double get() = weight * reps * sets
}

fun ExerciseHistoryEntry.toMap(): Map<String, Any> {
    return mapOf(
        "timestamp" to timestamp,
        "weight" to weight,
        "reps" to reps,
        "sets" to sets,
        "notes" to (notes ?: "")
    )
}