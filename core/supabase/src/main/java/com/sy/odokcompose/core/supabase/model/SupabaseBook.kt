package com.sy.odokcompose.core.supabase.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase에서 사용하는 Book 모델
 * 
 * Room의 BookEntity와 매핑되는 원격 데이터 구조
 */
@Serializable
data class SupabaseBook(
    @SerialName("item_id") val itemId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("isbn") val isbn: String,
    @SerialName("title") val title: String,
    @SerialName("author") val author: String,
    @SerialName("publisher") val publisher: String,
    @SerialName("published_date") val publishedDate: String = "",
    @SerialName("total_page_cnt") val totalPageCnt: Int = 0,
    @SerialName("current_page_cnt") val currentPageCnt: Int = 0,
    @SerialName("cover_image_url") val coverImageUrl: String = "",
    @SerialName("start_date") val startDate: String = "",
    @SerialName("end_date") val endDate: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("category") val category: String = "",
    @SerialName("rate") val rate: Float = 0.0f,
    @SerialName("finished_read_cnt") val finishedReadCnt: Int = 0,
    @SerialName("elapsed_time_in_seconds") val elapsedTimeInSeconds: Int = 0,
    @SerialName("created_at") val createdAt: String, // ISO 8601 format
    @SerialName("updated_at") val updatedAt: String  // ISO 8601 format
)