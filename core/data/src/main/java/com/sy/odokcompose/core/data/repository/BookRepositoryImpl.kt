package com.sy.odokcompose.core.data.repository

import com.sy.odokcompose.core.database.BookDao
import com.sy.odokcompose.core.database.entity.mapper.SearchBookEntityMapper
import com.sy.odokcompose.core.network.OdokNetworkDataSource
import com.sy.odokcompose.model.SearchBookUiModel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * BookRepository 인터페이스 구현체
 */
@Singleton
class BookRepositoryImpl @Inject constructor(
    private val networkDataSource: OdokNetworkDataSource,
    private val bookDao: BookDao
) : BookRepository {
    
    override suspend fun searchBooks(query: String, start: Int, maxResults: Int): List<SearchBookUiModel> {
        try {
            // 네트워크에서 책 검색 결과를 가져옵니다
            val searchResponse = networkDataSource.getSearchedBooks(query, start, maxResults)
            
            // 검색 결과를 UI 모델로 변환합니다
            return searchResponse.item.map { bookItem ->
                SearchBookUiModel(
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
        } catch (e: Exception) {
            // 오류 발생 시 빈 리스트 반환
            e.printStackTrace()
            return emptyList()
        }
    }
} 