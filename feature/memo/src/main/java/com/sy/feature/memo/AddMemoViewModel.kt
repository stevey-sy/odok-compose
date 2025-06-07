package com.sy.feature.memo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.sy.odokcompose.core.designsystem.icon.OdokIcons

@HiltViewModel
class AddMemoViewModel @Inject constructor() : ViewModel() {

    private val _text = MutableStateFlow(TextFieldValue(""))
    val text: StateFlow<TextFieldValue> = _text

    private val _selectedPaperType = MutableStateFlow(OdokIcons.WhitePaper)
    val selectedPaperType: StateFlow<Int> = _selectedPaperType.asStateFlow()

    private val paperTypes = listOf(
        OdokIcons.WhitePaper,
        OdokIcons.OldPaper,
        OdokIcons.DotPaper,
        OdokIcons.BlueSky,
        OdokIcons.YellowPaper
    )

    fun onTextChanged(newValue: TextFieldValue) {
        _text.value = newValue
    }

    fun updatePaperType(index: Int) {
        if (index in paperTypes.indices) {
            _selectedPaperType.value = paperTypes[index]
        }
    }

//    fun updatePageText(text: String) {
//        _pageText.value = text
//    }

}