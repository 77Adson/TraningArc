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

    private var currentSessionId: String? = null
    private var exercisesListener: ValueEventListener? = null
    private var sessionListener: ValueEventListener? = null

    fun getExercisesForSession(sessionId: String) {
        currentSessionId = sessionId
        setupRealTimeListeners(sessionId)
    }

    private fun setupRealTimeListeners(sessionId: String) {
        // Remove previous listeners to avoid duplicates
        exercisesListener?.let { database.removeEventListener(it) }
        sessionListener?.let { database.removeEventListener(it) }

        val userId = auth.currentUser?.uid ?: return

        // Listener for session data
        sessionListener = database.child("users/$userId/sessions/$sessionId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(sessionSnapshot: DataSnapshot) {
                    val session = sessionSnapshot.getValue(TrainingSession::class.java) ?: return
                    _currentSession.value = session
                    updateExercisesList(userId, session)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })

        // Listener for exercises data
        exercisesListener = database.child("users/$userId/exercises")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(exercisesSnapshot: DataSnapshot) {
                    _currentSession.value?.let { session ->
                        updateExercisesList(userId, session)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun updateExercisesList(userId: String, session: TrainingSession) {
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

    fun addExercise(sessionId: String, exercise: Exercise) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val exerciseId = database.child("users/$userId/exercises").push().key ?: return@launch

                // Add to general exercises collection
                database.child("users/$userId/exercises/$exerciseId")
                    .setValue(exercise)

                // Add reference to session with order
                val order = (_currentSession.value?.sessionExercises?.size ?: 0) + 1
                database.child("users/$userId/sessions/$sessionId/sessionExercises/$exerciseId")
                    .setValue(order)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

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

    fun deleteExercise(exerciseId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val sessionId = currentSessionId ?: return@launch

                // Remove from exercises collection
                database.child("users/$userId/exercises/$exerciseId")
                    .removeValue()

                // Remove from current session
                database.child("users/$userId/sessions/$sessionId/sessionExercises/$exerciseId")
                    .removeValue()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        exercisesListener?.let { database.removeEventListener(it) }
        sessionListener?.let { database.removeEventListener(it) }
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