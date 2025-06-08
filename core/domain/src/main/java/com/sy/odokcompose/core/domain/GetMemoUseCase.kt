package com.sy.odokcompose.core.domain

import com.sy.odokcompose.core.data.repository.MemoRepository
import com.sy.odokcompose.model.MemoUiModel
import javax.inject.Inject

class GetMemoUseCase @Inject constructor(
  private val memoRepository: MemoRepository
) {
    suspend operator fun invoke(memoId: Int): MemoUiModel? {
        return memoRepository.getMemoById(memoId)
    }
}