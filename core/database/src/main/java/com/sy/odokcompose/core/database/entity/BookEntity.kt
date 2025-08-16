package com.sy.odokcompose.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "books",
    indices = [
        Index(value = ["userId"]),  // userId로 자주 조회되므로 인덱스 추가
        Index(value = ["isbn"]),    // ISBN으로 중복 체크할 때 사용
        Index(value = ["createdAt"]) // 생성일 기준 정렬에 사용
    ]
)
data class BookEntity(
    @PrimaryKey val itemId: String = UUID.randomUUID().toString(), // UUID로 변경
    val userId: String = "none", // Supabase user ID
    val title: String,
    val author: String,
    val publisher: String,
    val category: String,
    val isbn: String,
    val coverImageUrl: String,
    val bookType: String = "",
    val totalPageCnt: Int = 0,
    val currentPageCnt: Int = 0,
    val challengePageCnt: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val elapsedTimeInSeconds: Int = 0,
    val completedReadingCnt: Int = 0,
    val description: String = "",
    val rate: Float = 0f,
    val finishedReadCnt: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)