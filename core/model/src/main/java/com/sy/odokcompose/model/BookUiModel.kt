package com.sy.odokcompose.model

data class BookUiModel(
    val itemId: Int = 0,
    val title: String = "",
    var author: String = "",
    val publisher: String = "",
    val isbn: String = "",
    val coverImageUrl: String = "",
    val bookType: String = "",
    val totalPageCnt: Int = 0,
    val currentPageCnt: Int = 0,
    val challengePageCnt: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val elapsedTimeInSeconds: Int = 0,
    val completedReadingCnt: Int = 0,
    var shelfPosition: Int = -1,
    var progressText: String = "",
    var progressPercentage: Int = 0,
    val memoList: List<MemoUiModel> = emptyList(),
    val finishedReadCnt: Int = 0,
) {
    init {
        // 초기화 블록에서 계산된 값들을 설정
        progressPercentage = calculateProgressPercentage()
        progressText = getProgressTextStr()
        author = getAuthorText()
    }

    private fun calculateProgressPercentage(): Int {
        return if (totalPageCnt > 0) (currentPageCnt * 100) / totalPageCnt else 0
    }

    fun getPercentageStr(): String {
        return "$progressPercentage%"
    }

    fun getProgressTextStr(): String {
        return "P. $currentPageCnt / $totalPageCnt"
    }

    fun getMemoListSizeStr(): String {
        return "Comments ${memoList.size}"
    }

    private fun getAuthorText(): String {
        return if (author.contains("(지은이)")) {
            author.substringBefore("(지은이)").trim()
        } else {
            author
        }
    }

    fun getElapsedTimeFormatted(): String {
        val hours = elapsedTimeInSeconds / 3600
        val minutes = (elapsedTimeInSeconds % 3600) / 60
        val seconds = elapsedTimeInSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
