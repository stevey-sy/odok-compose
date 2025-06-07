package com.sy.odokcompose.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    @PrimaryKey(autoGenerate = true) val memoId: Int = 0,
    val bookId: Int,
    val content: String,
    val pageNumber: Int,
    val backgroundId: String = "",
    val imgUrl: String = "",
    val createdAt: Long,
    val updatedAt: Long = System.currentTimeMillis()
)
