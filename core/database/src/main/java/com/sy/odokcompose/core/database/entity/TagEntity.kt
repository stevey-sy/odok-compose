package com.sy.odokcompose.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val tagId: Long = 0,
    val name: String,
    val backgroundColor: String,
    val textColor: String,
    val createdAt: Long = System.currentTimeMillis()
)
