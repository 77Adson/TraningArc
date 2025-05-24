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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ExerciseViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val _detail = MutableStateFlow<ExerciseDetail?>(null)
    val detail: StateFlow<ExerciseDetail?> = _detail.asStateFlow()

    fun getDetail(exerciseId: String) {
        val userId = auth.currentUser?.uid ?: return
        val ref = database.child("users").child(userId).child("exercises").child(exerciseId)

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
                Log.e("ExerciseVM", "Database error: ${error.message}")
            }
        })
    }

    fun updateExerciseDetail(detail: ExerciseDetail) {
        val userId = auth.currentUser?.uid ?: return
        val ref = database.child("users").child(userId).child("exercises").child(detail.workoutId)

        viewModelScope.launch {
            try {
                // Przygotuj mapę wartości do aktualizacji
                val updates = hashMapOf<String, Any>(
                    "name" to detail.name,
                    "weight" to detail.weight,
                    "reps" to detail.reps,
                    "sets" to detail.sets,
                    "notes" to (detail.notes ?: "")
                )

                // Wykonaj aktualizację
                ref.updateChildren(updates)
                    .addOnSuccessListener {
                        // Aktualizuj lokalny stan, zachowując historię
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

    fun addProgressEntry(entry: ExerciseHistoryEntry) {
        viewModelScope.launch {
            try {
                val currentDetail = _detail.value ?: run {
                    Log.e("ExerciseVM", "No current detail")
                    return@launch
                }

                val userId = auth.currentUser?.uid ?: run {
                    Log.e("ExerciseVM", "User not authenticated")
                    return@launch
                }

                // 1. Najpierw aktualizuj główne dane ćwiczenia
                val exerciseUpdate = mapOf(
                    "weight" to entry.weight,
                    "reps" to entry.reps,
                    "sets" to entry.sets,
                    "notes" to (entry.notes ?: "")
                )

                // 2. Przygotuj nowy wpis historii
                val newHistoryKey = database.child("history").push().key ?: run {
                    Log.e("ExerciseVM", "Couldn't generate key")
                    return@launch
                }

                val historyUpdate = mapOf(
                    "timestamp" to entry.timestamp,
                    "weight" to entry.weight,
                    "reps" to entry.reps,
                    "sets" to entry.sets,
                    "notes" to (entry.notes ?: "")
                )

                // 3. Przygotuj pełną aktualizację
                val updates = hashMapOf<String, Any>(
                    "users/$userId/exercises/${currentDetail.workoutId}" to exerciseUpdate,
                    "users/$userId/exercises/${currentDetail.workoutId}/history/$newHistoryKey" to historyUpdate
                )

                // 4. Wykonaj atomową aktualizację
                database.updateChildren(updates)
                    .addOnSuccessListener {
                        // Aktualizuj lokalny stan
                        _detail.value = currentDetail.copy(
                            weight = entry.weight,
                            reps = entry.reps,
                            sets = entry.sets,
                            notes = entry.notes,
                            history = currentDetail.history + (newHistoryKey to entry)
                        )
                        Log.d("ExerciseVM", "Successfully saved to Firebase")
                    }
                    .addOnFailureListener { e ->
                        Log.e("ExerciseVM", "Failed to save", e)
                    }
            } catch (e: Exception) {
                Log.e("ExerciseVM", "Error", e)
            }
        }
    }
}
