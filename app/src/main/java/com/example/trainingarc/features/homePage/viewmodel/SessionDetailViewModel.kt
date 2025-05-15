// features/homePage/viewmodel/SessionViewModel.kt
package com.example.trainingarc.features.homePage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
                    .get()
                    .addOnSuccessListener { snapshot ->
                        _sessionName.value = snapshot.getValue(String::class.java) ?: ""
                        _isLoading.value = false
                    }
                    .addOnFailureListener {
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }
}