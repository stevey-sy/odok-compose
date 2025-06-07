package com.sy.odokcompose.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MemoUiModel(
    val memoId: Int = 0,
    val bookId: Int,
    val content: String,
    val pageNumber: Int,
    val backgroundId: Int,
    var isExpanded: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val tags: List<TagUiModel> = emptyList()
) {
    fun getCreateDateText(): String {
        val formatter = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        return formatter.format(Date(createdAt))
    }
}