package com.sy.odokcompose

import com.google.firebase.auth.FirebaseUser

sealed interface AuthState {
    object Loading : AuthState
    object NotAuthenticated : AuthState
    data class Authenticated(val user: FirebaseUser) : AuthState
    data class Error(val exception: Exception) : AuthState
} 