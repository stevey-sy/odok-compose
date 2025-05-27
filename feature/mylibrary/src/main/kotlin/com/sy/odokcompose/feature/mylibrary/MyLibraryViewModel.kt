package com.sy.odokcompose.feature.mylibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.odokcompose.core.domain.GetMyBooksUseCase
import com.sy.odokcompose.model.BookUiModel
import com.sy.odokcompose.model.type.ShelfFilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MyLibraryViewModel @Inject constructor(
    private val getMyBooksUseCase : GetMyBooksUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyLibraryUiState())
    val uiState: StateFlow<MyLibraryUiState> = _uiState.asStateFlow()

    private val _shelfItems = MutableStateFlow<List<BookUiModel>>(emptyList())
    val shelfItems: StateFlow<List<BookUiModel>> = _shelfItems.asStateFlow()

    init {
        getShelfItems()
    }

    private fun getShelfItems(filter: ShelfFilterType = ShelfFilterType.NONE) {
        viewModelScope.launch {
            getMyBooksUseCase(filter).collect { books ->
                _shelfItems.value = books
            }
        }
    }
}

data class MyLibraryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)