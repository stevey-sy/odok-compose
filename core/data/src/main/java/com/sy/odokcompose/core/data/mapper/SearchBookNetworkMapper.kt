package com.sy.odokcompose.core.data.mapper

import com.sy.odokcompose.core.network.model.AladinBookItem
import com.sy.odokcompose.model.SearchBookUiModel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 네트워크 응답 모델인 BookItem을 UI에서 사용할 SearchBookUiModel로 변환하는 매퍼
 */
@Singleton
class SearchBookNetworkMapper @Inject constructor() {
    
    fun mapToUiModel(bookItem: AladinBookItem): SearchBookUiModel {
        return SearchBookUiModel(
            title = bookItem.title,
            author = bookItem.author,
            publisher = bookItem.publisher,
            isbn = bookItem.isbn,
            cover = bookItem.cover,
            page = bookItem.subInfo?.itemPage ?: 0,
            description = bookItem.description.toString(),
            rate = 0f // API에서 제공하지 않는 경우 기본값
        )
    }
} 