package com.sy.odokcompose.core.domain

import com.google.firebase.auth.FirebaseUser
import com.sy.odokcompose.core.data.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): FirebaseUser {
        return authRepository.signInWithGoogle(idToken)
    }
}