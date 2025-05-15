package com.example.trainingarc.features.homePage.viewmodel

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
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val _sessions = mutableStateOf<List<TrainingSession>>(emptyList())
    val sessions: State<List<TrainingSession>> = _sessions

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        loadSessions()
    }

    private fun loadSessions() {
        val userId = auth.currentUser?.uid ?: return
        _isLoading.value = true

        database.child("users").child(userId).child("sessions")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val sessionsList = mutableListOf<TrainingSession>()
                    for (sessionSnapshot in snapshot.children) {
                        val session = sessionSnapshot.getValue(TrainingSession::class.java)
                        session?.let {
                            sessionsList.add(it.copy(sessionId = sessionSnapshot.key ?: ""))
                        }
                    }
                    _sessions.value = sessionsList
                    _isLoading.value = false
                }

                override fun onCancelled(error: DatabaseError) {
                    _isLoading.value = false
                }
            })
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
}