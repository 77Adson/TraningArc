// AuthViewModel.kt
package com.example.trainingarc.features.auth.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _isLoggedIn = mutableStateOf(auth.currentUser != null)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    val currentUser: FirebaseUser? get() = auth.currentUser

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _isLoggedIn.value = firebaseAuth.currentUser != null
        }
    }

    fun loginUser(email: String, password: String, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    onComplete(true)
                } else {
                    _errorMessage.value = task.exception?.message ?: "Login failed"
                    onComplete(false)
                }
            }
    }

    fun registerUser(email: String, password: String, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    onComplete(true)
                } else {
                    _errorMessage.value = task.exception?.message ?: "Registration failed"
                    onComplete(false)
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}