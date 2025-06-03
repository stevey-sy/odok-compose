package com.sy.feature.timer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.odokcompose.core.domain.GetBookDetailUseCase
import com.sy.odokcompose.core.domain.UpdateBookUseCase
import com.sy.odokcompose.model.BookUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TimerUiState {
    data object BeforeReading : TimerUiState
    data object Reading : TimerUiState
    data object Paused : TimerUiState
    data object Completed : TimerUiState
}

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val getBookDetailUseCase: GetBookDetailUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimerUiState>(TimerUiState.BeforeReading)
    val uiState: StateFlow<TimerUiState> = _uiState

    private val _timerText = MutableStateFlow("00:00:00")
    val timerText: StateFlow<String> = _timerText.asStateFlow()

    private val _guideText = MutableStateFlow<String>("")
    val guideText: StateFlow<String> = _guideText.asStateFlow()

    private val _book = MutableStateFlow<BookUiModel>(BookUiModel())
    val book: StateFlow<BookUiModel> = _book

    private fun loadBookInfo(itemId: Int) {
        viewModelScope.launch {
            getBookDetailUseCase(itemId).collect { book ->
                _book.value = book
            }
        }
    }

    init {
        val itemId: Int = checkNotNull(savedStateHandle["itemId"]) { "itemId가 필요합니다." }
        loadBookInfo(itemId)
    }

}