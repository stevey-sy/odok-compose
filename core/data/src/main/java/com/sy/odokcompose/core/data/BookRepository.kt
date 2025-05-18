package com.sy.odokcompose.core.data

import androidx.annotation.WorkerThread
import com.sy.odokcompose.model.SearchBookUiModel
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    @WorkerThread
    fun fetchSearchedBookList(
        query: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
    ): Flow<List<SearchBookUiModel>>
}