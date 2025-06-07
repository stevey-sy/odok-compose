package com.sy.odokcompose.core.data.repository

import com.sy.odokcompose.core.database.entity.MemoEntity
import com.sy.odokcompose.model.MemoUiModel
import kotlinx.coroutines.flow.Flow

interface MemoRepository {
    /**
     *  itemId 로 메모 리스트 데이터를 가져옵니다.
     *  @param itemId 책 id
     */
    suspend fun getMemoListByBookId(itemId: Int): Flow<List<MemoUiModel>>

    /**
     *  memoId 로 메모 데이터를 가져옵니다.
     *  @param memoId 메모 id
     */
    suspend fun getMemoById(memoId: Int): Flow<MemoUiModel>

    /**
     *  memoId 로 메모 데이터를 삭제합니다.
     *  @param memoId 메모 id
     */
    suspend fun deleteMemoById(memoId: Int)

    /**
     *  memoId 로 메모 데이터를 업데이트 합니다.
     *  @param memoEntity 업데이트할 객체
     */
    suspend fun updateMemo(memoEntity: MemoEntity)
}