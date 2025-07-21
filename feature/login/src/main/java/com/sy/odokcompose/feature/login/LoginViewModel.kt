package com.sy.odokcompose.feature.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.odokcompose.core.domain.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {

    var loginUiState by mutableStateOf<LoginUiState>(LoginUiState.Idle)
        private set

    fun signIn(idToken: String) {
        viewModelScope.launch{
            loginUiState = LoginUiState.Loading
            try {
                val user = signInWithGoogleUseCase(idToken)
                loginUiState = LoginUiState.Success(user)
            } catch (e: Exception) {
                loginUiState = LoginUiState.Error(e)
            }
        }
    }

    fun handleError(exception: Exception) {
        loginUiState = LoginUiState.Error(exception)
    }
}