package com.sy.odokcompose.core.supabase.repository

import android.util.Log
import com.sy.odokcompose.core.supabase.client.SupabaseClientWrapper
import com.sy.odokcompose.core.supabase.model.SupabaseBook
import com.sy.odokcompose.core.supabase.model.SupabaseResponse
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supabase Book 원격 데이터 리포지토리
 * 
 * Supabase 데이터베이스와 직접 통신하여 Book 데이터를 관리합니다.
 */
@Singleton
class SupabaseBookRepository @Inject constructor(
    private val supabaseClient: SupabaseClientWrapper
) {
    
    companion object {
        private const val TAG = "SupabaseBookRepository"
        private const val TABLE_NAME = "books"
    }
    
    /**
     * 사용자의 모든 책 조회
     * 
     * @param userId 사용자 ID
     * @return 책 목록
     */
    suspend fun getAllBooks(userId: String): SupabaseResponse<List<SupabaseBook>> {
        return try {
            Log.d(TAG, "사용자 책 목록 조회 시작: $userId")
            
            val books = supabaseClient.postgrest
                .from(TABLE_NAME)
                .select()
                .decodeList<SupabaseBook>()
            
            // 클라이언트 사이드에서 필터링 (RLS가 서버에서 처리함)
            val userBooks = books.filter { it.userId == userId }
                .sortedByDescending { it.createdAt }
            
            Log.d(TAG, "책 목록 조회 성공: ${userBooks.size}개")
            SupabaseResponse.Success(userBooks)
            
        } catch (e: Exception) {
            Log.e(TAG, "책 목록 조회 실패", e)
            SupabaseResponse.Error(
                exception = e,
                message = "책 목록을 불러오는데 실패했습니다: ${e.message}"
            )
        }
    }
    
    /**
     * 특정 책 조회
     * 
     * @param itemId 책 ID
     * @param userId 사용자 ID
     * @return 책 정보
     */
    suspend fun getBook(itemId: String, userId: String): SupabaseResponse<SupabaseBook?> {
        return try {
            Log.d(TAG, "책 조회 시작: $itemId")
            
            val books = supabaseClient.postgrest
                .from(TABLE_NAME)
                .select()
                .decodeList<SupabaseBook>()
            
            val book = books.find { it.itemId == itemId && it.userId == userId }
            Log.d(TAG, "책 조회 완료: ${book?.title ?: "없음"}")
            
            SupabaseResponse.Success(book)
            
        } catch (e: Exception) {
            Log.e(TAG, "책 조회 실패", e)
            SupabaseResponse.Error(
                exception = e,
                message = "책 정보를 불러오는데 실패했습니다: ${e.message}"
            )
        }
    }
    
    /**
     * 책 추가
     * 
     * @param book 추가할 책 정보
     * @return 추가된 책 정보
     */
    suspend fun insertBook(book: SupabaseBook): SupabaseResponse<SupabaseBook> {
        return try {
            Log.d(TAG, "책 추가 시작: ${book.title}")
            
            val insertedBooks = supabaseClient.postgrest
                .from(TABLE_NAME)
                .insert(book)
                .decodeList<SupabaseBook>()
            
            val insertedBook = insertedBooks.first()
            Log.d(TAG, "책 추가 성공: ${insertedBook.title}")
            
            SupabaseResponse.Success(insertedBook)
            
        } catch (e: Exception) {
            Log.e(TAG, "책 추가 실패", e)
            SupabaseResponse.Error(
                exception = e,
                message = "책을 추가하는데 실패했습니다: ${e.message}"
            )
        }
    }
    
    /**
     * 책 업데이트
     * 
     * @param book 업데이트할 책 정보
     * @return 업데이트된 책 정보
     */
    suspend fun updateBook(book: SupabaseBook): SupabaseResponse<SupabaseBook> {
        return try {
            Log.d(TAG, "책 업데이트 시작: ${book.title}")
            
            val updatedBooks = supabaseClient.postgrest
                .from(TABLE_NAME)
                .update(book) {
                    filter {
                        eq("item_id", book.itemId)
                    }
                }
                .decodeList<SupabaseBook>()
            
            val updatedBook = updatedBooks.first()
            Log.d(TAG, "책 업데이트 성공: ${updatedBook.title}")
            
            SupabaseResponse.Success(updatedBook)
            
        } catch (e: Exception) {
            Log.e(TAG, "책 업데이트 실패", e)
            SupabaseResponse.Error(
                exception = e,
                message = "책 정보를 업데이트하는데 실패했습니다: ${e.message}"
            )
        }
    }
    
    /**
     * 책 삭제
     * 
     * @param itemId 삭제할 책 ID
     * @param userId 사용자 ID
     * @return 삭제 결과
     */
    suspend fun deleteBook(itemId: String, userId: String): SupabaseResponse<Unit> {
        return try {
            Log.d(TAG, "책 삭제 시작: $itemId")
            
            supabaseClient.postgrest
                .from(TABLE_NAME)
                .delete {
                    filter {
                        eq("item_id", itemId)
                    }
                }
            
            Log.d(TAG, "책 삭제 성공: $itemId")
            SupabaseResponse.Success(Unit)
            
        } catch (e: Exception) {
            Log.e(TAG, "책 삭제 실패", e)
            SupabaseResponse.Error(
                exception = e,
                message = "책을 삭제하는데 실패했습니다: ${e.message}"
            )
        }
    }
    
    /**
     * 여러 책 일괄 추가/업데이트 (Upsert)
     * 
     * @param books 추가/업데이트할 책 목록
     * @return 처리 결과
     */
    suspend fun upsertBooks(books: List<SupabaseBook>): SupabaseResponse<List<SupabaseBook>> {
        return try {
            Log.d(TAG, "책 일괄 upsert 시작: ${books.size}개")
            
            val upsertedBooks = supabaseClient.postgrest
                .from(TABLE_NAME)
                .upsert(books)
                .decodeList<SupabaseBook>()
            
            Log.d(TAG, "책 일괄 upsert 성공: ${upsertedBooks.size}개")
            SupabaseResponse.Success(upsertedBooks)
            
        } catch (e: Exception) {
            Log.e(TAG, "책 일괄 upsert 실패", e)
            SupabaseResponse.Error(
                exception = e,
                message = "책 목록을 동기화하는데 실패했습니다: ${e.message}"
            )
        }
    }
    
    /**
     * 특정 시간 이후 업데이트된 책 목록 조회 (동기화용)
     * 
     * @param userId 사용자 ID
     * @param lastSyncTime 마지막 동기화 시간 (밀리초 타임스탬프)
     * @return 업데이트된 책 목록
     */
    suspend fun getBooksUpdatedSince(
        userId: String,
        lastSyncTime: Long
    ): SupabaseResponse<List<SupabaseBook>> {
        return try {
            Log.d(TAG, "업데이트된 책 목록 조회: $lastSyncTime 이후")
            
            val books = supabaseClient.postgrest
                .from(TABLE_NAME)
                .select()
                .decodeList<SupabaseBook>()
            
            // 클라이언트 사이드에서 필터링
            val filteredBooks = books.filter { book ->
                book.userId == userId && book.updatedAt > lastSyncTime.toString()
            }.sortedBy { it.updatedAt }
            
            Log.d(TAG, "업데이트된 책 조회 성공: ${filteredBooks.size}개")
            SupabaseResponse.Success(filteredBooks)
            
        } catch (e: Exception) {
            Log.e(TAG, "업데이트된 책 조회 실패", e)
            SupabaseResponse.Error(
                exception = e,
                message = "동기화할 책 목록을 불러오는데 실패했습니다: ${e.message}"
            )
        }
    }
    
    /**
     * 사용자의 책 개수 조회
     * 
     * @param userId 사용자 ID
     * @return 책 개수
     */
    suspend fun getBookCount(userId: String): SupabaseResponse<Int> {
        return try {
            Log.d(TAG, "사용자 책 개수 조회: $userId")
            
            val books = supabaseClient.postgrest
                .from(TABLE_NAME)
                .select()
                .decodeList<SupabaseBook>()
            
            val count = books.count { it.userId == userId }
            Log.d(TAG, "책 개수 조회 성공: ${count}개")
            SupabaseResponse.Success(count)
            
        } catch (e: Exception) {
            Log.e(TAG, "책 개수 조회 실패", e)
            SupabaseResponse.Error(
                exception = e,
                message = "책 개수를 조회하는데 실패했습니다: ${e.message}"
            )
        }
    }
}