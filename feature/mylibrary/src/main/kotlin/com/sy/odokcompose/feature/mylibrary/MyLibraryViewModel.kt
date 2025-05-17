package com.sy.odokcompose.feature.mylibrary

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MyLibraryViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MyLibraryUiState())
    val uiState: StateFlow<MyLibraryUiState> = _uiState.asStateFlow()
}

data class MyLibraryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)