package com.sy.feature.memo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class AddMemoViewModel @Inject constructor() : ViewModel() {

    var memoText by mutableStateOf("")
        private set

    fun updateMemoText(text: String) {
        memoText = text
    }

//    fun updatePageText(text: String) {
//        _pageText.value = text
//    }

}