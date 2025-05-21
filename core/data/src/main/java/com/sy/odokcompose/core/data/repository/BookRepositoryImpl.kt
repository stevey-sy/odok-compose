package com.sy.odokcompose.core.data.repository

import com.sy.odokcompose.core.database.BookDao
import com.sy.odokcompose.core.database.entity.mapper.SearchBookEntityMapper
import com.sy.odokcompose.core.data.mapper.SearchBookNetworkMapper
import com.sy.odokcompose.core.network.OdokNetworkDataSource
import com.sy.odokcompose.model.SearchBookUiModel
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.atomic.AtomicInteger

/**
 * BookRepository 인터페이스 구현체
 */
@Singleton
class BookRepositoryImpl @Inject constructor(
    private val networkDataSource: OdokNetworkDataSource,
    private val bookDao: BookDao,
    private val searchBookNetworkMapper: SearchBookNetworkMapper
) : BookRepository {
    
    // 고유한 ID를 생성하기 위한 카운터
    private val idCounter = AtomicInteger(0)
    
    override suspend fun searchBooks(query: String, start: Int, maxResults: Int): List<SearchBookUiModel> {
        try {
            // 네트워크에서 책 검색 결과를 가져옵니다
            val searchResponse = networkDataSource.getSearchedBooks(query, start, maxResults)
            
            // 매퍼를 사용하여 네트워크 모델을 UI 모델로 변환하고 고유 ID 할당
            return searchResponse.item.mapIndexed { index, bookItem ->
                val uiModel = searchBookNetworkMapper.mapToUiModel(bookItem)
                // 고유한 ID 생성 및 할당 (ISBN과 인덱스 조합)
                uiModel.copy(id = idCounter.incrementAndGet())
            }
        } catch (e: Exception) {
            // 오류 발생 시 빈 리스트 반환
            e.printStackTrace()
            return emptyList()
        }
    }
} 