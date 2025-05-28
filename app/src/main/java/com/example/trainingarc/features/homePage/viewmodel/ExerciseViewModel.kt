package com.example.trainingarc.features.homePage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.trainingarc.features.homePage.model.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val _detail = MutableStateFlow<Exercise?>(null)
    val detail: StateFlow<Exercise?> = _detail.asStateFlow()

    fun getDetail(workoutId: String) {
        viewModelScope.launch {
            try {
                database.child("workoutDetails/$workoutId")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        _detail.value = snapshot.getValue(Exercise::class.java)
                            ?: Exercise(workoutId, "")
                    }
            } catch (e: Exception) {
                // Handle error
                _detail.value = Exercise(workoutId, "")
            }
        }
    }

    fun updateDescription(workoutId: String, description: String) {
        viewModelScope.launch {
            try {
                val updatedDetail = Exercise(workoutId, description)
                database.child("workoutDetails/$workoutId").setValue(updatedDetail)
                _detail.value = updatedDetail
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
                // Obsługa błędu
            }
        }
    }
}