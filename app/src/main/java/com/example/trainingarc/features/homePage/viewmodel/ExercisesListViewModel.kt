package com.example.trainingarc.features.homePage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.trainingarc.features.homePage.model.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExercisesListViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts.asStateFlow()
    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

    fun getExercisesList(sessionId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                database.child("users/$userId/sessions/$sessionId/workouts")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val workoutsList = snapshot.children.mapNotNull { child ->
                                child.getValue(Workout::class.java)
                            }
                            _workouts.value = workoutsList
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    })
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun addExercise(sessionId: String, name: String) {
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

    fun updateExerciseName(sessionId: String, workoutId: String, newName: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                database.child("users/$userId/sessions/$sessionId/workouts/$workoutId/name")
                    .setValue(newName)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteExerciese(sessionId: String, workoutId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                database.child("users/$userId/sessions/$sessionId/workouts/$workoutId")
                    .removeValue()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun setCurrentSession(sessionId: String) {
        _currentSessionId.value = sessionId
    }

    fun deleteCurrentSession(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val sessionId = _currentSessionId.value ?: return@launch

                // First delete all workouts in this session
                database.child("users/$userId/sessions/$sessionId/workouts")
                    .removeValue()
                    .addOnSuccessListener {
                        // Then delete the session itself
                        database.child("users/$userId/sessions/$sessionId")
                            .removeValue()
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e ->
                                onFailure(Exception("Failed to delete session", e))
                            }
                    }
                    .addOnFailureListener { e ->
                        onFailure(Exception("Failed to delete workouts", e))
                    }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}