package com.example.trainingarc.features.homePage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.trainingarc.features.homePage.model.TrainingSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val _sessions = MutableStateFlow<List<Pair<TrainingSession, String>>>(emptyList())
    val sessions: StateFlow<List<Pair<TrainingSession, String>>> = _sessions.asStateFlow()

    init {
        loadSessions()
    }

    fun loadSessions() {
        val userId = auth.currentUser?.uid ?: return

        database.child("users/$userId/sessions")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val sessionsList = snapshot.children.mapNotNull { childSnapshot ->
                        val session = childSnapshot.getValue(TrainingSession::class.java)
                        val key = childSnapshot.key
                        if (session != null && key != null) {
                            session to key
                        } else {
                            null
                        }
                    }
                    _sessions.value = sessionsList
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error loading sessions", error.toException())
                }
            })
    }

    fun createNewSession(sessionName: String, onComplete: (String?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onComplete(null)

        database.child("users/$userId/sessions").push().apply {
            setValue(TrainingSession(sessionName = sessionName))
                .addOnSuccessListener { onComplete(key) }
                .addOnFailureListener { onComplete(null) }
        }
    }

    fun deleteSession(sessionKey: String, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onComplete(false)

        database.child("users/$userId/sessions/$sessionKey")
            .removeValue()
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }
}