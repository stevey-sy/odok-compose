package com.sy.odokcompose.core.data.repository

import com.sy.odokcompose.core.database.entity.BookEntity
import com.sy.odokcompose.model.BookUiModel
import com.sy.odokcompose.model.type.ShelfFilterType
import kotlinx.coroutines.flow.Flow

interface BookShelfRepository {
    /**
     *  나의 서재의 책들을 조회합니다.
     */
    suspend fun getShelfItems(filterBy: ShelfFilterType): Flow<List<BookUiModel>>

    /**
     *  itemId 로 책 데이터를 가져옵니다.
     *  @param itemId 책 id
     */
    suspend fun getBookById(itemId: Int): Flow<BookUiModel>

    /**
     *  itemId 로 책 데이터를 삭제합니다.
     *  @param itemId 책 id
     */
    suspend fun deleteBookById(itemId: Int)

    /**
     *  책 데이터를 업데이트합니다.
     *  @param itemId 책 id
     */
    suspend fun updateReadingProgress(itemId: Int, page: Int, elapsedTime: Int)

    /**
     *  책 데이터를 업데이트합니다.
     *  @param bookEntity 책 객체
     */
    suspend fun updateBook(bookEntity: BookEntity)




}