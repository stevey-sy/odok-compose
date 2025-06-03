package com.sy.feature.timer

import androidx.lifecycle.ViewModel
import com.sy.odokcompose.core.domain.GetBookDetailUseCase
import com.sy.odokcompose.core.domain.UpdateBookUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed interface TimerUiState {
    data object BeforeReading : TimerUiState
    data object Reading : TimerUiState
    data object Paused : TimerUiState
    data object Completed : TimerUiState
}

class TimerViewModel @Inject constructor(
    private val getBookDetailUseCase: GetBookDetailUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<TimerUiState>(TimerUiState.BeforeReading)
    val uiState: StateFlow<TimerUiState> = _uiState
}