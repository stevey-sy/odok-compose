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

@HiltViewModel
class AddMemoViewModel @Inject constructor(
    private val saveMemoUseCase: SaveMemoUseCase,
    private val savedStateHandle : SavedStateHandle
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle["itemId"]) { "itemId가 필요합니다." }

    init {
        // 초기 배경색/텍스트색 설정은 ViewModel에서 제거 (Screen에서 애니메이션 초기값으로 처리)
    }

    private val _pageText = MutableStateFlow(TextFieldValue(""))
    val pageText: StateFlow<TextFieldValue> = _pageText.asStateFlow()

    private val _memoText = MutableStateFlow(TextFieldValue(""))
    val memoText: StateFlow<TextFieldValue> = _memoText.asStateFlow()

    private val _selectedPaperType = MutableStateFlow(OdokIcons.WhitePaper)
    val selectedPaperType: StateFlow<Int> = _selectedPaperType.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val paperTypes = listOf(
        OdokIcons.WhitePaper,
        OdokIcons.OldPaper,
        OdokIcons.DotPaper,
        OdokIcons.BlueSky,
        OdokIcons.YellowPaper
    )

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
            val page = _pageText.value.text.toInt()
            val memo = _memoText.value.text
            val paperType = _selectedPaperType.value
            
            if (memo.isNotBlank()) {
                saveMemoUseCase(itemId, page, memo, paperType)
            }
        } finally {
            _isSaving.value = false
        }
    }
}