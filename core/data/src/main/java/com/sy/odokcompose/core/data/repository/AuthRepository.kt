package com.sy.odokcompose.core.data.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): FirebaseUser
}