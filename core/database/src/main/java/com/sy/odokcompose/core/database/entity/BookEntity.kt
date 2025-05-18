package com.sy.odokcompose.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val itemId: Int = 0, // 자동 생성되는 ID
    val title: String,
    val author: String,
    val publisher: String,
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
    val rate: Float = 0f
)