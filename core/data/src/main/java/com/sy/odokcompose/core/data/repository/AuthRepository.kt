package com.sy.odokcompose.core.data.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): FirebaseUser
    suspend fun signOut()
    suspend fun isUserLoggedIn(): Boolean
    suspend fun setUserLoggedIn(isLoggedIn: Boolean)
    fun getLoginStatusFlow(): Flow<Boolean>
}