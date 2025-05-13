package com.example.trainingarc.features.profilePage.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {
    private val auth = Firebase.auth

    private val _userData = mutableStateOf<FirebaseUser?>(auth.currentUser)
    val userData: State<FirebaseUser?> = _userData

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _greetingMessage = mutableStateOf("Welcome")
    val greetingMessage: State<String> = _greetingMessage

    init {
        // You can add additional initialization here
        updateGreeting()
    }

    private fun updateGreeting() {
        _userData.value?.let { user ->
            _greetingMessage.value = "Welcome back, ${user.email?.split("@")?.first() ?: "User"}!"
        }
    }

    fun refreshUserData() {
        _userData.value = auth.currentUser
        updateGreeting()
    }
}