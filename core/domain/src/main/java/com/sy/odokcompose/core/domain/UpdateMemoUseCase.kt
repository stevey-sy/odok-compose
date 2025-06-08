package com.sy.odokcompose.core.domain

import com.sy.odokcompose.core.data.local.datasource.MemoLocalDataSource
import com.sy.odokcompose.core.database.entity.MemoEntity
import com.sy.odokcompose.core.database.entity.mapper.MemoEntityMapper
import com.sy.odokcompose.model.MemoUiModel
import javax.inject.Inject

sealed class UpdateMemoResult {
    object Success: UpdateMemoResult()
    data class Error(val message: String): UpdateMemoResult()
}

class UpdateMemoUseCase @Inject constructor(
    private val memoLocalDataSource: MemoLocalDataSource
) {
    suspend operator fun invoke(memoUiModel: MemoUiModel): UpdateMemoResult {
        return try {
            val memoEntity = MemoEntityMapper.modelToEntity(memoUiModel)
            memoLocalDataSource.update(memoEntity)
            UpdateMemoResult.Success
        } catch (e: Exception) {
            UpdateMemoResult.Error(e.message ?:  "알 수 없는 오류가 발생했습니다.")
        }
    }
}