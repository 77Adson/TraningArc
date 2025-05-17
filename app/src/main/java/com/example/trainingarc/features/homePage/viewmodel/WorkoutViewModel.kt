package com.example.trainingarc.features.homePage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.trainingarc.features.homePage.model.Workout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts

    fun getWorkouts(sessionId: String): Flow<List<Workout>> {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                database.child("users/$userId/sessions/$sessionId/workouts")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val workoutsList = snapshot.children.mapNotNull { child ->
                            child.getValue(Workout::class.java)
                        }
                        _workouts.value = workoutsList
                    }
            } catch (e: Exception) {
                // Handle error
            }
        }
        return _workouts
    }

    fun addWorkout(sessionId: String, name: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val workoutId = database.child("workouts").push().key ?: return@launch

                val newWorkout = Workout(
                    id = workoutId,
                    name = name
                )

                database.child("users/$userId/sessions/$sessionId/workouts/$workoutId")
                    .setValue(newWorkout)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}