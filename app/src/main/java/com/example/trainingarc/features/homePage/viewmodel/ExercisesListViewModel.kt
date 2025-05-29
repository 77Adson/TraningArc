package com.example.trainingarc.features.homePage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.trainingarc.features.homePage.model.Exercise
import com.example.trainingarc.features.homePage.model.ExerciseWithId
import com.example.trainingarc.features.homePage.model.TrainingSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExercisesListViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val _exercises = MutableStateFlow<List<ExerciseWithId>>(emptyList())
    val exercises: StateFlow<List<ExerciseWithId>> = _exercises.asStateFlow()

    private val _currentSession = MutableStateFlow<TrainingSession?>(null)
    val currentSession: StateFlow<TrainingSession?> = _currentSession.asStateFlow()

    // Get all exercises for a session
    fun getExercisesForSession(sessionId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                database.child("users/$userId/sessions/$sessionId")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(sessionSnapshot: DataSnapshot) {
                            val session = sessionSnapshot.getValue(TrainingSession::class.java) ?: return
                            _currentSession.value = session

                            database.child("users/$userId/exercises")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(exercisesSnapshot: DataSnapshot) {
                                        val allExercises = exercisesSnapshot.children.mapNotNull {
                                            ExerciseWithId(
                                                id = it.key ?: "",
                                                exercise = it.getValue(Exercise::class.java) ?: return@mapNotNull null
                                            )
                                        }

                                        // Filter to only include exercises in this session
                                        val sessionExercises = allExercises.filter { exercise ->
                                            session.sessionExercises.containsKey(exercise.id)
                                        }.sortedBy { exercise ->
                                            session.sessionExercises[exercise.id]
                                        }

                                        _exercises.value = sessionExercises
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        // Handle error
                                    }
                                })
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

    // Add a new exercise to both the exercises list and the session
    fun addExercise(sessionId: String, exercise: Exercise) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val exerciseId = database.child("exercises").push().key ?: return@launch

                // Add to general exercises collection (without exerciseId in the Exercise object)
                database.child("users/$userId/exercises/$exerciseId")
                    .setValue(exercise)

                // Add reference to session with order
                val order = (_currentSession.value?.sessionExercises?.size ?: 0) + 1
                database.child("users/$userId/sessions/$sessionId/sessionExercises/$exerciseId")
                    .setValue(order)

                // Update local session data
                _currentSession.value?.let { current ->
                    val updatedExercises = current.sessionExercises.toMutableMap()
                    updatedExercises[exerciseId] = order
                    _currentSession.value = current.copy(sessionExercises = updatedExercises)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Update an exercise
    fun updateExercise(exerciseId: String, exercise: Exercise) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                database.child("users/$userId/exercises/$exerciseId")
                    .setValue(exercise)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Delete an exercise (from both exercises collection and all sessions)
    fun deleteExercise(exerciseId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                // Delete from exercises collection
                database.child("users/$userId/exercises/$exerciseId")
                    .removeValue()

                // Delete from all sessions (you might want to add a transaction here)
                database.child("users/$userId/sessions")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach { sessionSnapshot ->
                                val sessionId = sessionSnapshot.key ?: return@forEach
                                database.child("users/$userId/sessions/$sessionId/sessionExercises/$exerciseId")
                                    .removeValue()
                            }
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

    fun deleteCurrentSession(
        sessionKey: String,  // Now explicitly takes the Firebase key
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                // First delete all exercise references from the session
                database.child("users/$userId/sessions/$sessionKey/sessionExercises")
                    .removeValue()
                    .addOnSuccessListener {
                        // Then delete the session itself
                        database.child("users/$userId/sessions/$sessionKey")
                            .removeValue()
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onFailure(Exception(e)) }
                    }
                    .addOnFailureListener { e -> onFailure(Exception(e)) }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}