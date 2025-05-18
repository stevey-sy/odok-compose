package com.sy.odokcompose.model

data class TagUiModel(
    val tagId: Long = 0,
    val name: String,
    val backgroundColor: String = "#FFFFFF",
    val textColor: String = "#000000",
    val createdAt: Long = System.currentTimeMillis()
)
