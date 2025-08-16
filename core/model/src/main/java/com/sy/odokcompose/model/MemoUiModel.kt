package com.sy.odokcompose.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MemoUiModel(
    val userId: String,
    val memoId: String,
    val bookId: String,
    val content: String,
    val pageNumber: Int,
    val backgroundId: String = "",
    var isExpanded: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val tags: List<TagUiModel> = emptyList()
) {
    fun getCreateDateText(): String {
        val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        return formatter.format(Date(createdAt))
    }
}