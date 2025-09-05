package com.sy.odokcompose.core.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.sy.odokcompose.core.data.local.UserPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userPreferences: UserPreferences
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return suspendCoroutine { cont ->
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { result -> 
                    userPreferences.setUserLoggedIn(true)
                    cont.resume(result.user!!) 
                }
                .addOnFailureListener {e-> cont.resumeWithException(e)}
        }
    }
    
    override suspend fun signOut() {
        firebaseAuth.signOut()
        userPreferences.setUserLoggedIn(false)
    }
    
    override suspend fun isUserLoggedIn(): Boolean {
        return userPreferences.isUserLoggedIn()
    }
    
    override suspend fun setUserLoggedIn(isLoggedIn: Boolean) {
        userPreferences.setUserLoggedIn(isLoggedIn)
    }
    
    override fun getLoginStatusFlow(): Flow<Boolean> {
        return userPreferences.loginStatusFlow
    }

}