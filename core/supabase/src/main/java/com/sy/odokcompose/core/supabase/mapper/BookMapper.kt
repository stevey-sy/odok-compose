package com.sy.odokcompose.core.supabase.mapper

import com.sy.odokcompose.core.database.entity.BookEntity
import com.sy.odokcompose.core.supabase.model.SupabaseBook
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * BookEntity ↔ SupabaseBook 변환 매퍼
 */
object BookMapper {
    
    private val isoFormatter = DateTimeFormatter.ISO_INSTANT
    
    /**
     * BookEntity를 SupabaseBook으로 변환
     */
    fun toSupabaseBook(entity: BookEntity): SupabaseBook {
        return SupabaseBook(
            itemId = entity.itemId,
            userId = entity.userId,
            isbn = entity.isbn,
            title = entity.title,
            author = entity.author,
            publisher = entity.publisher,
            publishedDate = entity.publishedDate,
            totalPageCnt = entity.totalPageCnt,
            currentPageCnt = entity.currentPageCnt,
            coverImageUrl = entity.coverImageUrl,
            startDate = entity.startDate,
            endDate = entity.endDate,
            description = entity.description,
            category = entity.category,
            rate = entity.rate,
            finishedReadCnt = entity.finishedReadCnt,
            elapsedTimeInSeconds = entity.elapsedTimeInSeconds,
            createdAt = timestampToIsoString(entity.createdAt),
            updatedAt = timestampToIsoString(entity.updatedAt)
        )
    }
    
    /**
     * SupabaseBook을 BookEntity로 변환
     */
    fun toBookEntity(supabaseBook: SupabaseBook): BookEntity {
        return BookEntity(
            itemId = supabaseBook.itemId,
            userId = supabaseBook.userId,
            isbn = supabaseBook.isbn,
            title = supabaseBook.title,
            author = supabaseBook.author,
            publisher = supabaseBook.publisher,
            publishedDate = supabaseBook.publishedDate,
            totalPageCnt = supabaseBook.totalPageCnt,
            currentPageCnt = supabaseBook.currentPageCnt,
            coverImageUrl = supabaseBook.coverImageUrl,
            startDate = supabaseBook.startDate,
            endDate = supabaseBook.endDate,
            description = supabaseBook.description,
            category = supabaseBook.category,
            rate = supabaseBook.rate,
            finishedReadCnt = supabaseBook.finishedReadCnt,
            elapsedTimeInSeconds = supabaseBook.elapsedTimeInSeconds,
            createdAt = isoStringToTimestamp(supabaseBook.createdAt),
            updatedAt = isoStringToTimestamp(supabaseBook.updatedAt)
        )
    }
    
    /**
     * BookEntity 리스트를 SupabaseBook 리스트로 변환
     */
    fun toSupabaseBooks(entities: List<BookEntity>): List<SupabaseBook> {
        return entities.map { toSupabaseBook(it) }
    }
    
    /**
     * SupabaseBook 리스트를 BookEntity 리스트로 변환
     */
    fun toBookEntities(supabaseBooks: List<SupabaseBook>): List<BookEntity> {
        return supabaseBooks.map { toBookEntity(it) }
    }
    
    /**
     * Unix timestamp (milliseconds)를 ISO 8601 문자열로 변환
     */
    private fun timestampToIsoString(timestamp: Long): String {
        return Instant.ofEpochMilli(timestamp).atOffset(ZoneOffset.UTC).format(isoFormatter)
    }
    
    /**
     * ISO 8601 문자열을 Unix timestamp (milliseconds)로 변환
     */
    private fun isoStringToTimestamp(isoString: String): Long {
        return try {
            Instant.from(isoFormatter.parse(isoString)).toEpochMilli()
        } catch (e: Exception) {
            // 파싱 실패 시 현재 시간 반환
            System.currentTimeMillis()
        }
    }
}