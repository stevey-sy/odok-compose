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
                val updatedBooks = updateShelfPosition(books)
                val paddedBooks = addDummyItems(updatedBooks)
                _shelfItems.value = paddedBooks
            }
        }
    }

    private fun updateShelfPosition(originalList: List<BookUiModel>): List<BookUiModel> {
        return originalList.mapIndexed { index, book ->
            book.copy(shelfPosition = index % 3)
        }
    }

    private fun addDummyItems(originalList: List<BookUiModel>): List<BookUiModel> {
        val remainder = originalList.size % 3
        return if (remainder == 0) {
            originalList
        } else {
            val paddingCount = 3 - remainder
            val padded = originalList.toMutableList()
            repeat(paddingCount) {
                padded.add(
                    BookUiModel(
                        itemId = -1 * (it + 1),
                    )
                )
            }
            padded
        }
    }
}

data class MyLibraryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)