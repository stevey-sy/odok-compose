package com.sy.odokcompose.core.data.local.datasource


import com.sy.odokcompose.core.database.entity.MemoEntity
import kotlinx.coroutines.flow.Flow

interface MemoLocalDataSource : LocalDataSource<MemoEntity> {
    suspend fun getMemoListByBookId(itemId: Int): Flow<List<MemoEntity>>
    suspend fun getMemoById(memoId: Int): MemoEntity?
    suspend fun deleteMemoById(memoId: Int)
    suspend fun updateMemo(memoEntity: MemoEntity)
}