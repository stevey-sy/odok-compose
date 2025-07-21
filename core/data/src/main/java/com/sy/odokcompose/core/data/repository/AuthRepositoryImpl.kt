package com.sy.odokcompose.core.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return suspendCoroutine { cont ->
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { result -> cont.resume(result.user!!) }
                .addOnFailureListener {e-> cont.resumeWithException(e)}
        }
    }

}