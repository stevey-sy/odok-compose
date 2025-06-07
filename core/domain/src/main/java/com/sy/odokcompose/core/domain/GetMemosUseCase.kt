package com.sy.odokcompose.core.domain

import com.sy.odokcompose.core.data.repository.MemoRepository
import com.sy.odokcompose.model.MemoUiModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMemosUseCase @Inject constructor(
    private val memoRepository: MemoRepository
) {
    suspend operator fun invoke(itemId: Int): Flow<List<MemoUiModel>> {
        return memoRepository.getMemoListByBookId(itemId)
    }
}