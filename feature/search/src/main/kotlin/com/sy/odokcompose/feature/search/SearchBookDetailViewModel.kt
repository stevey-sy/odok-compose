package com.sy.odokcompose.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sy.odokcompose.core.domain.GetSearchBookDetailUseCase
import com.sy.odokcompose.core.domain.SaveBookUseCase
import com.sy.odokcompose.model.SearchBookUiModel
import com.sy.odokcompose.core.database.entity.BookEntity
import com.sy.odokcompose.core.database.entity.mapper.SearchBookItemMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchDetailUiState(
    val isLoading: Boolean = false,
    val getBookDetailSuccess: SearchBookUiModel? = null,
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class SearchDetailViewModel @Inject constructor(
    private val getBookDetailUseCase: GetSearchBookDetailUseCase,
    private val saveBookUseCase: SaveBookUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SearchDetailUiState())
    val uiState: StateFlow<SearchDetailUiState> = _uiState.asStateFlow()
    
    fun loadBookDetail(isbn: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val bookDetail = getBookDetailUseCase(isbn)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        getBookDetailSuccess = bookDetail
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "알 수 없는 오류가 발생했습니다."
                    )
                }
            }
        }
    }

    fun saveBook() {
        val bookDetail = uiState.value.getBookDetailSuccess ?: return
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true, error = null) }
                
                val bookEntity = SearchBookItemMapper.modelToEntity(bookDetail)
                saveBookUseCase(bookEntity)
                
                _uiState.update { 
                    it.copy(
                        isSaving = false,
                        saveSuccess = true
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "책 저장 중 오류가 발생했습니다."
                    )
                }
            }
        }
    }
} 