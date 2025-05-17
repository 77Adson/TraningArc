package com.example.trainingarc.features.homePage.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingarc.features.homePage.model.TrainingSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val _sessions = MutableStateFlow<List<TrainingSession>>(emptyList())
    val sessions: StateFlow<List<TrainingSession>> = _sessions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var sessionsListener: ValueEventListener? = null

    init {
        loadSessions()
    }

    fun loadSessions() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                database.child("users/$userId/sessions")
                    .orderByChild("lastUpdated") // Sortowanie po dacie aktualizacji
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val sessionsList = snapshot.children.mapNotNull { child ->
                                child.getValue(TrainingSession::class.java)?.copy(
                                    sessionId = child.key ?: ""
                                )
                            }
                            _sessions.value = sessionsList
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Firebase", "Error loading sessions", error.toException())
                        }
                    })
            } catch (e: Exception) {
                Log.e("ViewModel", "Error loading sessions", e)
            }
        }
    }

    fun createNewSession(sessionName: String, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val sessionRef = database.child("users").child(userId).child("sessions").push()
                val newSession = TrainingSession(
                    sessionId = sessionRef.key ?: "",
                    sessionName = sessionName
                )
                sessionRef.setValue(newSession)
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun deleteSession(sessionId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                database.child("users/$userId/sessions/$sessionId")
                    .removeValue()
                    .addOnSuccessListener {
                        _sessions.value = _sessions.value.filter { it.sessionId != sessionId }
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firebase", "Delete failed", e)
                    }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error deleting session", e)
            }
        }
    }
}