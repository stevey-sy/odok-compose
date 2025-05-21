package com.sy.odokcompose.core.data.repository

import com.sy.odokcompose.model.SearchBookUiModel
import kotlinx.coroutines.flow.Flow

/**
 * 책 정보에 대한 액세스를 제공하는 Repository 인터페이스
 */
interface BookRepository {
    /**
     * 책을 검색합니다
     * 
     * @param query 검색어
     * @param start 검색 시작 인덱스
     * @param maxResults 최대 결과 개수
     * @return 검색된 책 목록
     */
    suspend fun searchBooks(query: String, start: Int = 1, maxResults: Int = 10): List<SearchBookUiModel>

    /**
     * 검색된 책의 상세 정보를 가져옵니다.
     *
     * @param itemId isbn
     * @return 검색된 책 상세 정보
     */
    suspend fun getBookDetail(itemId: String): SearchBookUiModel

} 