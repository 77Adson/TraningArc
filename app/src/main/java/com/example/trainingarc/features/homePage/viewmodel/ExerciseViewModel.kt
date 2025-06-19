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
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val userId get() = auth.currentUser?.uid ?: ""

    private val _detail = MutableStateFlow<ExerciseWithId?>(null)
    val detail: StateFlow<ExerciseWithId?> = _detail.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Pobieranie szczegółów ćwiczenia
    fun getExerciseDetail(exerciseId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = database.child("users/$userId/exercises/$exerciseId")
                    .get()
                    .await()

                _detail.value = ExerciseWithId(
                    id = exerciseId,
                    exercise = snapshot.getValue(Exercise::class.java) ?: Exercise()
                )
            } catch (e: Exception) {
                _detail.value = ExerciseWithId(
                    id = exerciseId,
                    exercise = Exercise()
                )
                // Consider adding error state handling here
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateExerciseStats(
        exerciseId: String,
        sets: Int,
        reps: Int,
        weight: Float,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val newScore = weight * reps * sets
                val timestamp = System.currentTimeMillis()
                val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp))

                val updates = hashMapOf<String, Any>(
                    "sets" to sets,
                    "reps" to reps,
                    "weight" to weight,
                    "history/$dateKey" to newScore
                )

                database.child("users/$userId/exercises/$exerciseId")
                    .updateChildren(updates)
                    .await()

                // Update local state
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
            } catch (e: Exception) {
                // Handle error (consider adding error state)
                e.printStackTrace()
            }
        }
    }

    fun updateDescription(exerciseId: String, description: String) {
        viewModelScope.launch {
            try {
                database.child("users/$userId/exercises/$exerciseId/description")
                    .setValue(description)
                    .await()

                _detail.value?.let { current ->
                    _detail.value = current.copy(
                        exercise = current.exercise.copy(
                            description = description
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteExercise(
        sessionId: String,
        exerciseId: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                // Remove from exercises collection
                database.child("users/$userId/exercises/$exerciseId")
                    .removeValue()
                    .await()

                // Remove from session (if sessionId provided)
                if (sessionId.isNotBlank()) {
                    database.child("users/$userId/sessions/$sessionId/sessionExercises/$exerciseId")
                        .removeValue()
                        .await()
                }

                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearExerciseDetail() {
        _detail.value = null
    }
}