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

class ExerciseViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val userId get() = auth.currentUser?.uid ?: ""

    private val _detail = MutableStateFlow<ExerciseWithId?>(null)
    val detail: StateFlow<ExerciseWithId?> = _detail.asStateFlow()

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

    fun updateExerciseStats(
        exerciseId: String,
        sets: Int,
        reps: Int,
        weight: Float,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val updates = mapOf(
                    "sets" to sets,
                    "reps" to reps,
                    "weight" to weight
                )

                database.child("users/$userId/exercises/$exerciseId")
                    .updateChildren(updates)
                    .addOnSuccessListener {
                        // Update local state
                        _detail.value?.let { current ->
                            _detail.value = current.copy(
                                exercise = current.exercise.copy(
                                    sets = sets,
                                    reps = reps,
                                    weight = weight
                                )
                            )
                        }
                        onSuccess()
                    }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateDescription(exerciseId: String, description: String) {
        viewModelScope.launch {
            try {
                database.child("users/$userId/exercises/$exerciseId/description")
                    .setValue(description)
                    .addOnSuccessListener {
                        // Update local state
                        _detail.value?.let { current ->
                            _detail.value = current.copy(
                                exercise = current.exercise.copy(
                                    description = description
                                )
                            )
                        }
                    }
            } catch (e: Exception) {
                // Handle error
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
                // Remove from exercises collection
                database.child("users/$userId/exercises/$exerciseId")
                    .removeValue()

                // Remove from session's exercise list
                database.child("users/$userId/sessions/$sessionId/sessionExercises/$exerciseId")
                    .removeValue()
                    .addOnSuccessListener { onSuccess() }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}