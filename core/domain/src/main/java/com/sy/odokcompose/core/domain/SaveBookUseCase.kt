package com.sy.odokcompose.core.domain

import com.sy.odokcompose.core.data.local.datasource.BookLocalDataSource
import com.sy.odokcompose.core.database.entity.BookEntity
import javax.inject.Inject

class SaveBookUseCase @Inject constructor(
    private val bookLocalDataSource: BookLocalDataSource
) {
    suspend operator fun invoke(book: BookEntity) {
        bookLocalDataSource.insert(book)
    }
}