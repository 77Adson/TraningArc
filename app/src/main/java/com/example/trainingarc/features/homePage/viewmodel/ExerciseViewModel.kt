package com.example.trainingarc.features.homePage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.trainingarc.features.homePage.model.Exercise
import com.example.trainingarc.features.homePage.model.ExerciseWithId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val userId get() = auth.currentUser?.uid ?: ""

    private val _detail = MutableStateFlow<ExerciseWithId?>(null)
    val detail: StateFlow<ExerciseWithId?> = _detail.asStateFlow()

    // Pobieranie szczegółów ćwiczenia
    fun getExerciseDetail(exerciseId: String) {
        viewModelScope.launch {
            try {
                database.child("users/$userId/exercises/$exerciseId")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        _detail.value = ExerciseWithId(
                            id = exerciseId,
                            exercise = snapshot.getValue(Exercise::class.java) ?: Exercise()
                        )
                    }
                    .addOnFailureListener {
                        _detail.value = ExerciseWithId(exerciseId, Exercise())
                    }
            } catch (e: Exception) {
                _detail.value = ExerciseWithId(exerciseId, Exercise())
            }
        }
    }

    // Aktualizacja statystyk ćwiczenia z automatycznym dodaniem wpisu do historii
    fun updateExerciseStats(
        exerciseId: String,
        sets: Int,
        reps: Int,
        weight: Float,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                // Oblicz nowy wynik
                val newScore = weight * reps * sets
                val timestamp = System.currentTimeMillis()
                val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp))

                // Przygotuj aktualizacje
                val updates = hashMapOf<String, Any>(
                    "sets" to sets,
                    "reps" to reps,
                    "weight" to weight,
                    "history/$dateKey" to newScore
                )

                // Wykonaj aktualizację w bazie danych
                database.child("users/$userId/exercises/$exerciseId")
                    .updateChildren(updates)
                    .addOnSuccessListener {
                        // Aktualizacja stanu lokalnego
                        _detail.value?.let { current ->
                            val newHistory = current.exercise.history.toMutableMap().apply {
                                put(dateKey, newScore)
                            }

                            _detail.value = current.copy(
                                exercise = current.exercise.copy(
                                    sets = sets,
                                    reps = reps,
                                    weight = weight,
                                    history = newHistory
                                )
                            )
                        }
                        onSuccess()
                    }
            } catch (e: Exception) {
                // Obsługa błędu
                e.printStackTrace()
            }
        }
    }

    // Ręczne dodanie wpisu do historii (np. dla treningów bez zmiany statystyk)
    fun addHistoryEntry(
        exerciseId: String,
        date: Date = Date(),
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _detail.value?.let { current ->
                val score = current.currentScore()
                val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

                val updates = mapOf(
                    "history/$dateKey" to score
                )

                database.child("users/$userId/exercises/$exerciseId")
                    .updateChildren(updates)
                    .addOnSuccessListener {
                        val newHistory = current.exercise.history.toMutableMap().apply {
                            put(dateKey, score)
                        }

                        _detail.value = current.copy(
                            exercise = current.exercise.copy(
                                history = newHistory
                            )
                        )
                        onSuccess()
                    }
            }
        }
    }

    // Pozostałe metody pozostają bez zmian (updateDescription, deleteExercise itp.)
    fun updateDescription(exerciseId: String, description: String) {
        viewModelScope.launch {
            try {
                database.child("users/$userId/exercises/$exerciseId/description")
                    .setValue(description)
                    .addOnSuccessListener {
                        _detail.value?.let { current ->
                            _detail.value = current.copy(
                                exercise = current.exercise.copy(
                                    description = description
                                )
                            )
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteExercise(
        sessionId: String,
        exerciseId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                database.child("users/$userId/exercises/$exerciseId")
                    .removeValue()

                database.child("users/$userId/sessions/$sessionId/sessionExercises/$exerciseId")
                    .removeValue()
                    .addOnSuccessListener { onSuccess() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}