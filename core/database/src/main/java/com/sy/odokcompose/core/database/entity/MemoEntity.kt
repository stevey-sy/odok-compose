package com.sy.odokcompose.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "memos",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["itemId"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MemoEntity(
    @PrimaryKey val memoId: String = UUID.randomUUID().toString(), // UUID로 변경
    val userId: String, // Supabase user ID
    val bookId: String, // BookEntity의 UUID 참조
    val content: String,
    val pageNumber: Int,
    val backgroundId: String = "",
    val imgUrl: String = "",
    val createdAt: Long,
    val updatedAt: Long = System.currentTimeMillis()
)
