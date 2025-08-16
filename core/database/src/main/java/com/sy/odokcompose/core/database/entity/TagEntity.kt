package com.sy.odokcompose.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "tags",
    indices = [
        Index(value = ["userId"]),  // userId로 자주 조회되므로 인덱스 추가
        Index(value = ["name"]),    // 태그명으로 검색할 때 사용
        Index(value = ["createdAt"]) // 생성일 기준 정렬에 사용
    ]
)
data class TagEntity(
    @PrimaryKey val tagId: String = UUID.randomUUID().toString(), // UUID로 변경
    val userId: String, // Supabase user ID
    val name: String,
    val backgroundColor: String,
    val textColor: String,
    val createdAt: Long = System.currentTimeMillis()
)
