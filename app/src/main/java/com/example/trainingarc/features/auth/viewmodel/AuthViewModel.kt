package com.example.trainingarc.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingarc.features.auth.model.AuthError
import com.example.trainingarc.features.auth.model.AuthState
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _state = MutableStateFlow(
        AuthState(isLoggedIn = auth.currentUser != null)
    )
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _state.value = _state.value.copy(
                isLoggedIn = firebaseAuth.currentUser != null
            )
        }
    }

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        _state.value = _state.value.copy(
            isLoading = true,
            error = null
        )

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _state.value = _state.value.copy(isLoading = false)

                if (!task.isSuccessful) {
                    _state.value = _state.value.copy(
                        error = task.exception?.toAuthError() ?: AuthError.UnknownError
                    )
                }
            }
    }

    fun registerUser(email: String, password: String, confirmPassword: String) = viewModelScope.launch {
        // Clear previous errors and set loading state
        _state.value = _state.value.copy(
            isLoading = true,
            error = null
        )

        // First validate locally
        validatePassword(password, confirmPassword)?.let { validationError ->
            _state.value = _state.value.copy(
                isLoading = false,
                error = validationError
            )
            return@launch
        }

        // Only proceed to Firebase if validation passes
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _state.value = _state.value.copy(isLoading = false)

                if (!task.isSuccessful) {
                    _state.value = _state.value.copy(
                        error = task.exception?.toAuthError() ?: AuthError.UnknownError
                    )
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun validatePassword(password: String, confirmPassword: String): AuthError? {
        return when {
            password != confirmPassword -> AuthError.PasswordMismatch
            password.length < 6 -> AuthError.WeakPassword
            else -> null
        }
    }
}

// Extension function to convert Firebase exceptions to our AuthError
private fun Exception.toAuthError(): AuthError = when (this) {
    is FirebaseAuthInvalidUserException -> AuthError.UserNotFound
    is FirebaseAuthInvalidCredentialsException -> AuthError.WrongPassword
    is FirebaseAuthUserCollisionException -> AuthError.EmailAlreadyInUse
    is FirebaseAuthWeakPasswordException -> AuthError.WeakPassword
    is FirebaseNetworkException -> AuthError.NetworkError
    else -> AuthError.FirebaseError(message ?: "Authentication failed")
}