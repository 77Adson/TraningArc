package com.example.trainingarc.features.auth.model

sealed class AuthError {
    data class FirebaseError(val message: String) : AuthError()
    data object InvalidEmail : AuthError()
    data object WeakPassword : AuthError()
    data object UserDisabled : AuthError()
    data object UserNotFound : AuthError()
    data object WrongPassword : AuthError()
    data object EmailAlreadyInUse : AuthError()
    data object OperationNotAllowed : AuthError()
    data object NetworkError : AuthError()
    data object UnknownError : AuthError()
    data object PasswordMismatch : AuthError()


    fun toUserMessage(): String = when (this) {
        is FirebaseError -> message
        InvalidEmail -> "Invalid email format"
        WeakPassword -> "Password should be at least 6 characters"
        UserDisabled -> "This account has been disabled"
        UserNotFound -> "No account found with this email"
        WrongPassword -> "Incorrect password"
        EmailAlreadyInUse -> "Email already in use"
        OperationNotAllowed -> "Operation not allowed"
        NetworkError -> "Network error occurred"
        UnknownError -> "An unknown error occurred"
        InvalidEmail -> "Invalid email format"
        PasswordMismatch -> "Passwords do not match"
    }
}