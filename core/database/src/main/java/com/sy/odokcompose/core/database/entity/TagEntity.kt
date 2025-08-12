package com.sy.odokcompose.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val tagId: String = UUID.randomUUID().toString(), // UUID로 변경
    val userId: String, // Supabase user ID
    val name: String,
    val backgroundColor: String,
    val textColor: String,
    val createdAt: Long = System.currentTimeMillis()
)
