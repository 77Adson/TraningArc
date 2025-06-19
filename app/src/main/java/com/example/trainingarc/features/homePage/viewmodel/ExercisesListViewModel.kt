package com.example.trainingarc.features.homePage.viewmodel

import android.util.Log
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
import com.google.firebase.Firebase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ExercisesListViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val _currentSession = MutableStateFlow<TrainingSession?>(null)
    val currentSession: StateFlow<TrainingSession?> = _currentSession.asStateFlow()

    // Should be StateFlow or Flow
    private val _exercises = MutableStateFlow<List<ExerciseWithId>>(emptyList())
    val exercises: StateFlow<List<ExerciseWithId>> = _exercises.asStateFlow()

    private val _allExercises = MutableStateFlow<List<ExerciseWithId>>(emptyList())
    val allExercises: StateFlow<List<ExerciseWithId>> = _allExercises.asStateFlow()

    private var currentSessionId: String? = null
    private var exercisesListener: ValueEventListener? = null
    private var sessionListener: ValueEventListener? = null

    fun setCurrentSessionId(sessionId: String) {
        currentSessionId = sessionId
        setupRealTimeListeners(sessionId)
    }

    fun getExercisesForSession(sessionId: String) {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            Log.e("ExercisesListViewModel", "User not authenticated, cannot get exercises.")
            return
        }
        viewModelScope.launch {
            try {
                val sessionExercisesRef = Firebase.database.reference
                    .child("users")
                    .child(currentUserId)
                    .child("sessions")
                    .child(sessionId)
                    .child("sessionExercises")

                val exercisesRef = Firebase.database.reference
                    .child("users")
                    .child(currentUserId)
                    .child("exercises")

                val sessionExercises = sessionExercisesRef.get().await()
                    .getValue(object : GenericTypeIndicator<Map<String, Int>>() {}) ?: emptyMap()

                val exercisesList = mutableListOf<ExerciseWithId>()

                for ((exerciseId, order) in sessionExercises) {
                    val exerciseSnapshot = exercisesRef.child(exerciseId).get().await()
                    val exercise = exerciseSnapshot.getValue(Exercise::class.java)
                    if (exercise != null) {
                        exercisesList.add(ExerciseWithId(exerciseId, exercise, order))
                    }
                }

                _exercises.value = exercisesList.sortedBy { it.order }
            } catch (e: Exception) {
                Log.e("ExercisesListViewModel", "Error getting exercises", e)
            }
        }
    }

    private fun setupRealTimeListeners(sessionId: String) {
        exercisesListener?.let { database.removeEventListener(it) }
        sessionListener?.let { database.removeEventListener(it) }

        val userId = auth.currentUser?.uid ?: return
        currentSessionId = sessionId

        // Session listener
        sessionListener = database.child("users/$userId/sessions/$sessionId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val session = snapshot.getValue(TrainingSession::class.java)
                    _currentSession.value = session
                    session?.let { updateExercisesList(userId, it) }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ExercisesListViewModel", "Session listener cancelled", error.toException())
                }
            })

        // Exercises listener
        exercisesListener = database.child("users/$userId/exercises")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _currentSession.value?.let { session ->
                        updateExercisesList(userId, session)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ExercisesListViewModel", "Exercises listener cancelled", error.toException())
                }
            })
    }

    private fun updateExercisesList(userId: String, session: TrainingSession) {
        viewModelScope.launch {
            try {
                val exercisesSnapshot = database.child("users/$userId/exercises")
                    .get()
                    .await()

                val sessionExercises = session.sessionExercises
                val exercisesList = mutableListOf<ExerciseWithId>()

                for ((exerciseId, order) in sessionExercises) {
                    val exercise = exercisesSnapshot.child(exerciseId)
                        .getValue(Exercise::class.java) ?: continue
                    exercisesList.add(ExerciseWithId(exerciseId, exercise, order))
                }

                _exercises.value = exercisesList.sortedBy { it.order }
            } catch (e: Exception) {
                Log.e("ExercisesListViewModel", "Error updating exercises list", e)
            }
        }
    }

    fun addExercise(sessionId: String, exercise: Exercise) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val exercisesRef = database.child("users/$userId/exercises")
                val sessionsRef = database.child("users/$userId/sessions/$sessionId")

                // Generate new exercise ID
                val exerciseId = exercisesRef.push().key ?: return@launch

                // Get current session to determine the next order number
                val sessionSnapshot = sessionsRef.get().await()
                val session = sessionSnapshot.getValue(TrainingSession::class.java) ?: return@launch

                // Calculate the next order number
                val nextOrder = (session.sessionExercises.values.maxOrNull() ?: 0) + 1

                // Create a transaction to ensure both operations succeed or fail together
                val updates = hashMapOf<String, Any>(
                    "users/$userId/exercises/$exerciseId" to exercise,
                    "users/$userId/sessions/$sessionId/sessionExercises/$exerciseId" to nextOrder
                )

                database.updateChildren(updates).await()

                // Refresh the exercises list
                updateExercisesList(userId, session)
            } catch (e: Exception) {
                Log.e("ExercisesListViewModel", "Error adding exercise", e)
                // You might want to expose this error to the UI
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

                // Get current order to update remaining exercises
                val sessionRef = database.child("users/$userId/sessions/$sessionId/sessionExercises")
                val currentOrder = sessionRef.get().await()
                    .getValue(object : GenericTypeIndicator<Map<String, Int>>() {})
                    ?: emptyMap()

                val deletedOrder = currentOrder[exerciseId] ?: return@launch

                // Remove the exercise from session
                sessionRef.child(exerciseId).removeValue()

                // Update orders of exercises that were after the deleted one
                val updates = currentOrder
                    .filter { it.value > deletedOrder }
                    .mapValues { it.value - 1 }

                if (updates.isNotEmpty()) {
                    sessionRef.updateChildren(updates).await()
                }

                // Optionally delete from exercises collection if not used elsewhere
                // database.child("users/$userId/exercises/$exerciseId").removeValue()

                // Refresh the list
                getExercisesForSession(sessionId)
            } catch (e: Exception) {
                Log.e("ExercisesListViewModel", "Error deleting exercise", e)
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

    fun loadAllUserExercises(userId: String, sessionId: String) {
        viewModelScope.launch {
            try {
                // Get all exercises
                val exercisesSnapshot = Firebase.database.reference
                    .child("users")
                    .child(userId)
                    .child("exercises")
                    .get()
                    .await()

                // Get current session exercises to exclude
                val sessionSnapshot = Firebase.database.reference
                    .child("users")
                    .child(userId)
                    .child("sessions")
                    .child(sessionId)
                    .child("sessionExercises")
                    .get()
                    .await()

                val currentExerciseIds = sessionSnapshot.children
                    .mapNotNull { it.key }
                    .toSet()

                _allExercises.value = exercisesSnapshot.children.mapNotNull { child ->
                    val exerciseId = child.key ?: return@mapNotNull null
                    if (exerciseId !in currentExerciseIds) {
                        val exercise = child.getValue(Exercise::class.java)
                        exercise?.let {
                            ExerciseWithId(
                                id = exerciseId,
                                exercise = it,
                                order = 0 // Default order for new exercises
                            )
                        }
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                Log.e("ExercisesListViewModel", "Error loading exercises", e)
                // Optionally update UI to show error state
                _allExercises.value = emptyList()
            }
        }
    }

    fun addExistingExerciseToSession(userId: String, sessionId: String, exerciseId: String) {
        viewModelScope.launch {
            try {
                val sessionRef = Firebase.database.reference
                    .child("users")
                    .child(userId)
                    .child("sessions")
                    .child(sessionId)

                val currentExercises = sessionRef.child("sessionExercises")
                    .get()
                    .await()
                    .getValue(object : GenericTypeIndicator<Map<String, Int>>() {})
                    ?: emptyMap()

                val updatedExercises = currentExercises.toMutableMap().apply {
                    put(exerciseId, (this.values.maxOrNull() ?: 0) + 1)
                }

                sessionRef.child("sessionExercises").setValue(updatedExercises)
                getExercisesForSession(sessionId) // Refresh the current list
            } catch (e: Exception) {
                Log.e("ExercisesListViewModel", "Error adding exercise", e)
                // Handle error
            }
        }
    }

    fun moveExerciseUp(sessionId: String, exerciseId: String) {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            Log.e("ExercisesListViewModel", "User not authenticated, cannot move exercise.")
            // Optionally, you could throw an exception or return a result indicating failure
            return
        }

        viewModelScope.launch {
            try {
                val sessionRef = Firebase.database.reference
                    .child("users")
                    .child(currentUserId)
                    .child("sessions")
                    .child(sessionId)
                    .child("sessionExercises")

                val currentOrder = sessionRef.get().await()
                    .getValue(object : GenericTypeIndicator<Map<String, Int>>() {}) ?: emptyMap()

                val exerciseOrder = currentOrder[exerciseId] ?: return@launch
                if (exerciseOrder <= 1) return@launch // Already at top

                val updatedOrder = currentOrder.toMutableMap().apply {
                    // Find the exercise that's currently above this one
                    val exerciseAbove = entries.find { it.value == exerciseOrder - 1 }?.key
                    exerciseAbove?.let {
                        put(it, exerciseOrder) // Move the above exercise down
                    }
                    put(exerciseId, exerciseOrder - 1) // Move this exercise up
                }

                sessionRef.setValue(updatedOrder)
                getExercisesForSession(sessionId) // Refresh the list
            } catch (e: Exception) {
                Log.e("ExercisesListViewModel", "Error moving exercise up", e)
            }
        }
    }

    fun moveExerciseDown(sessionId: String, exerciseId: String) {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            Log.e("ExercisesListViewModel", "User not authenticated, cannot move exercise.")
            // Optionally, you could throw an exception or return a result indicating failure
            return
        }

        viewModelScope.launch {
            try {
                val sessionRef = Firebase.database.reference
                    .child("users")
                    .child(currentUserId)
                    .child("sessions")
                    .child(sessionId)
                    .child("sessionExercises")

                val currentOrder = sessionRef.get().await()
                    .getValue(object : GenericTypeIndicator<Map<String, Int>>() {}) ?: emptyMap()

                val exerciseOrder = currentOrder[exerciseId] ?: return@launch
                if (exerciseOrder >= currentOrder.size) return@launch // Already at bottom

                val updatedOrder = currentOrder.toMutableMap().apply {
                    // Find the exercise that's currently below this one
                    val exerciseBelow = entries.find { it.value == exerciseOrder + 1 }?.key
                    exerciseBelow?.let {
                        put(it, exerciseOrder) // Move the below exercise up
                    }
                    put(exerciseId, exerciseOrder + 1) // Move this exercise down
                }

                sessionRef.setValue(updatedOrder)
                getExercisesForSession(sessionId) // Refresh the list
            } catch (e: Exception) {
                Log.e("ExercisesListViewModel", "Error moving exercise down", e)
            }
        }
    }
}