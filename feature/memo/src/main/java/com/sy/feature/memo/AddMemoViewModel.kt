package com.sy.feature.memo

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.sy.odokcompose.core.designsystem.icon.OdokIcons
import com.sy.odokcompose.core.domain.SaveMemoUseCase
import com.sy.odokcompose.model.MemoUiModel
import java.text.SimpleDateFormat
import java.util.*

sealed class AddMemoUiState {
    object CreateMode : AddMemoUiState()
    object EditMode : AddMemoUiState()
    object SaveSuccess : AddMemoUiState()
    data class Error(val message: String) : AddMemoUiState()
}

@HiltViewModel
class AddMemoViewModel @Inject constructor(
    private val saveMemoUseCase: SaveMemoUseCase,
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle["itemId"]) { "itemId가 필요합니다." }

    private val _uiState = MutableStateFlow<AddMemoUiState>(AddMemoUiState.CreateMode)
    val uiState: StateFlow<AddMemoUiState> = _uiState.asStateFlow()

    private val _pageText = MutableStateFlow(TextFieldValue(""))
    val pageText: StateFlow<TextFieldValue> = _pageText.asStateFlow()

    private val _memoText = MutableStateFlow(TextFieldValue(""))
    val memoText: StateFlow<TextFieldValue> = _memoText.asStateFlow()

    private val _selectedPaperType = MutableStateFlow("white_paper")
    val selectedPaperType: StateFlow<String> = _selectedPaperType.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val paperTypes = listOf(
        "white_paper",
        "old_paper",
        "dot_paper",
        "blue_sky",
        "yellow_paper"
    )

    fun getTodayDate(): String {
        return SimpleDateFormat("yyyy. MM. dd", Locale.getDefault()).format(Date())
    }

    fun updatePageText(newValue: TextFieldValue) {
        _pageText.value = newValue
    }

    fun updateMemoText(newValue: TextFieldValue) {
        _memoText.value = newValue
    }

    fun updatePaperType(index: Int) {
        if (index in paperTypes.indices) {
            _selectedPaperType.value = paperTypes[index]
        }
    }

    suspend fun saveMemo() {
        if (_isSaving.value) return
        
        try {
            _isSaving.value = true
            val page = _pageText.value.text.ifEmpty { "0" }.toInt()
            val memo = _memoText.value.text
            val paperType = _selectedPaperType.value
            
            if (memo.isNotBlank()) {
                saveMemoUseCase(itemId, page, memo, paperType)
                _uiState.value = AddMemoUiState.SaveSuccess
            }
        } catch (e: Exception) {
            _uiState.value = AddMemoUiState.Error(e.message ?: "메모 저장에 실패했습니다.")
        } finally {
            _isSaving.value = false
        }
    }
}