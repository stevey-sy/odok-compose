package com.sy.odokcompose.feature.login

import com.google.firebase.auth.FirebaseUser

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val user: FirebaseUser) : LoginUiState
    data class Error(val exception: Exception) : LoginUiState
}