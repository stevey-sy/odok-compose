package com.sy.odokcompose.core.domain

import com.sy.odokcompose.core.data.local.datasource.MemoLocalDataSource
import com.sy.odokcompose.core.database.entity.MemoEntity
import javax.inject.Inject


sealed class SaveMemoResult {
    object Success : SaveMemoResult()
    data class Error(val message: String) : SaveMemoResult()
}
class SaveMemoUseCase @Inject constructor(
    private val memoLocalDataSource: MemoLocalDataSource
) {
    suspend operator fun invoke(bookId: Int, page:Int, content:String, background:Int): SaveMemoResult {
        return try {
            val memo = MemoEntity(
                bookId = bookId,
                content = content,
                pageNumber = page,
                background = background,
                createdAt = System.currentTimeMillis()
            )
            memoLocalDataSource.insert(memo)
            SaveMemoResult.Success
        } catch (e: Exception) {
            SaveMemoResult.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
        }
    }
}