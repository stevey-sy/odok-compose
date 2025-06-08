package com.sy.odokcompose.core.domain

import com.sy.odokcompose.core.data.repository.MemoRepository
import javax.inject.Inject


sealed class DeleteMemoResult {
    object Success: DeleteMemoResult()
    data class Error(val meesage: String) : DeleteMemoResult()
}

class DeleteMemoUseCase @Inject constructor(
    private val memoRepository: MemoRepository
) {
    suspend operator fun invoke(memoId: Int) : DeleteMemoResult {
        return try {
            memoRepository.deleteMemoById(memoId)
            DeleteMemoResult.Success
        } catch (e: Exception) {
            DeleteMemoResult.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
        }
    }
}