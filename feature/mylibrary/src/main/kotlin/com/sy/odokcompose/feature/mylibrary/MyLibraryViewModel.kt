package com.sy.odokcompose.feature.mylibrary

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyLibraryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyLibraryUiState())
    val uiState: StateFlow<MyLibraryUiState> = _uiState.asStateFlow()
}

data class MyLibraryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)