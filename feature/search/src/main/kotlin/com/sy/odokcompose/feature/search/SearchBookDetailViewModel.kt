package com.sy.odokcompose.feature.search

import androidx.lifecycle.ViewModel
import com.sy.odokcompose.core.data.repository.BookRepository
import com.sy.odokcompose.model.SearchBookUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchBookDetailViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    val book: SearchBookUiModel = SearchBookUiModel()

    fun addBookToMyLibrary() {

    }


}