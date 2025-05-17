// features/homePage/viewmodel/SessionViewModel.kt
package com.example.trainingarc.features.homePage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SessionViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val _sessionName = MutableStateFlow("")
    val sessionName: StateFlow<String> = _sessionName

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadSessionData(sessionId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = auth.currentUser?.uid ?: return@launch

                database.child("users/$userId/sessions/$sessionId/name")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            _sessionName.value = snapshot.getValue(String::class.java) ?: ""
                            _isLoading.value = false
                        }

                        override fun onCancelled(error: DatabaseError) {
                            _isLoading.value = false
                        }
                    })
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun updateSessionName(sessionId: String, newName: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                database.child("users/$userId/sessions/$sessionId/name")
                    .setValue(newName)
                    .addOnFailureListener { e ->
                    }
            } catch (e: Exception) {
                // Obsługa błędu
            }
        }
    }

    fun deleteSession(sessionId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                database.child("users/$userId/sessions/$sessionId/workouts")
                    .removeValue()
                    .addOnSuccessListener {
                        database.child("users/$userId/sessions/$sessionId")
                            .removeValue()
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e ->
                            }
                    }
                    .addOnFailureListener { e ->
                    }
            } catch (e: Exception) {
                // Obsługa błędu
            }
        }
    }
}