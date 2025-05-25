package com.example.trainingarc.features.homePage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingarc.features.homePage.model.ExerciseDetail
import com.example.trainingarc.features.homePage.model.ExerciseHistoryEntry
import com.example.trainingarc.features.homePage.model.toMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ExerciseViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val _detail = MutableStateFlow<ExerciseDetail?>(null)
    val detail: StateFlow<ExerciseDetail?> = _detail.asStateFlow()

    fun getDetail(sessionId: String, exerciseId: String) {
        val userId = auth.currentUser?.uid ?: return

        val ref = database.child("users")
            .child(userId)
            .child("sessions")
            .child(sessionId)
            .child("workouts")
            .child(exerciseId)

        // Zmienione logowanie - ręczne budowanie ścieżki zamiast ref.path
        Log.d("ExerciseVM", "Loading from: users/$userId/sessions/$sessionId/workouts/$exerciseId")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val historyMap = mutableMapOf<String, ExerciseHistoryEntry>()
                    snapshot.child("history").children.forEach { historySnapshot ->
                        historySnapshot.getValue(ExerciseHistoryEntry::class.java)?.let { entry ->
                            historyMap[historySnapshot.key ?: ""] = entry
                        }
                    }

                    val detail = ExerciseDetail(
                        workoutId = exerciseId,
                        name = snapshot.child("name").getValue(String::class.java) ?: "",
                        weight = snapshot.child("weight").getValue(Double::class.java) ?: 0.0,
                        reps = snapshot.child("reps").getValue(Int::class.java) ?: 0,
                        sets = snapshot.child("sets").getValue(Int::class.java) ?: 0,
                        notes = snapshot.child("notes").getValue(String::class.java),
                        history = historyMap
                    )
                    _detail.value = detail
                } catch (e: Exception) {
                    Log.e("ExerciseVM", "Error parsing data", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ExerciseVM", "Database error", error.toException())
            }
        })
    }

    fun updateExerciseDetail(detail: ExerciseDetail) {
        val userId = auth.currentUser?.uid ?: return
        val ref = database.child("users").child(userId).child("exercises").child(detail.workoutId)

        viewModelScope.launch {
            try {

                val updates = hashMapOf<String, Any>(
                    "name" to detail.name,
                    "weight" to detail.weight,
                    "reps" to detail.reps,
                    "sets" to detail.sets,
                    "notes" to (detail.notes ?: "")
                )

                ref.updateChildren(updates)
                    .addOnSuccessListener {
                        _detail.value = _detail.value?.copy(
                            name = detail.name,
                            weight = detail.weight,
                            reps = detail.reps,
                            sets = detail.sets,
                            notes = detail.notes
                        ) ?: detail
                    }
                    .addOnFailureListener { e ->
                        Log.e("ExerciseVM", "Failed to update exercise", e)
                    }
            } catch (e: Exception) {
                Log.e("ExerciseVM", "Error updating exercise", e)
            }
        }
    }

    fun addProgressEntry(sessionId: String, entry: ExerciseHistoryEntry) {
        val userId = auth.currentUser?.uid ?: return
        val currentDetail = _detail.value ?: return

        val newHistoryKey = database.child("history").push().key ?: return

        val historyData = hashMapOf<String, Any>(
            "timestamp" to ServerValue.TIMESTAMP,
            "weight" to entry.weight,
            "reps" to entry.reps,
            "sets" to entry.sets,
            "notes" to (entry.notes ?: "")
        )

        val historyRef = database
            .child("users")
            .child(userId)
            .child("sessions")
            .child(sessionId)
            .child("workouts")
            .child(currentDetail.workoutId)
            .child("history")
            .child(newHistoryKey)

        historyRef.setValue(historyData)
            .addOnSuccessListener {
                val updatedHistory = currentDetail.history.toMutableMap().apply {
                    put(newHistoryKey, entry.copy(timestamp = System.currentTimeMillis()))
                }
                _detail.value = currentDetail.copy(history = updatedHistory)
            }
            .addOnFailureListener { e ->
                Log.e("ExerciseVM", "Failed to save history", e)
            }
    }
}