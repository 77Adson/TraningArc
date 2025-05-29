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

    // Change to use ExerciseWithId
    private val _detail = MutableStateFlow<ExerciseWithId?>(null)
    val detail: StateFlow<ExerciseWithId?> = _detail.asStateFlow()

    fun getDetail(workoutId: String) {
        viewModelScope.launch {
            try {
                database.child("workoutDetails/$workoutId")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        _detail.value = ExerciseWithId(
                            id = workoutId,
                            exercise = snapshot.getValue(Exercise::class.java) ?: Exercise()
                        )
                    }
            } catch (e: Exception) {
                // Handle error
                _detail.value = ExerciseWithId(workoutId, Exercise())
            }
        }
    }

    fun updateDescription(workoutId: String, description: String) {
        viewModelScope.launch {
            try {
                val currentExercise = _detail.value?.exercise ?: Exercise()
                val updatedExercise = currentExercise.copy(description = description)
                database.child("workoutDetails/$workoutId").setValue(updatedExercise)
                _detail.value = ExerciseWithId(workoutId, updatedExercise)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteWorkoutDetail(workoutId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                database.child("workoutDetails/$workoutId").removeValue()
                    .addOnSuccessListener { onSuccess() }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}