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

@HiltViewModel
class AddMemoViewModel @Inject constructor() : ViewModel() {

    private val _text = MutableStateFlow(TextFieldValue(""))
    val text: StateFlow<TextFieldValue> = _text

    fun onTextChanged(newValue: TextFieldValue) {
        _text.value = newValue
    }

//    fun updatePageText(text: String) {
//        _pageText.value = text
//    }

}