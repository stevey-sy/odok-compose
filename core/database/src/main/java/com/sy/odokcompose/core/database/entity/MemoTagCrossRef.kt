package com.sy.odokcompose.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "memo_tags",
    primaryKeys = ["memoId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = MemoEntity::class,
            parentColumns = ["memoId"],
            childColumns = ["memoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["tagId"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["memoId"]),  // memoId 외래키 인덱스
        Index(value = ["tagId"])    // tagId 외래키 인덱스
    ]
)
data class MemoTagCrossRef(
    val memoId: String, // MemoEntity의 UUID 참조
    val tagId: String   // TagEntity의 UUID 참조
)
