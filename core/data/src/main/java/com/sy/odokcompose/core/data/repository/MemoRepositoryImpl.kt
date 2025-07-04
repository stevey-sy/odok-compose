package com.sy.odokcompose.core.data.repository

import com.sy.odokcompose.core.data.local.datasource.MemoLocalDataSource
import com.sy.odokcompose.core.database.entity.MemoEntity
import com.sy.odokcompose.core.database.entity.mapper.BookEntityMapper
import com.sy.odokcompose.core.database.entity.mapper.MemoEntityMapper
import com.sy.odokcompose.model.MemoUiModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MemoRepositoryImpl @Inject constructor(
    private val memoLocalDataSource: MemoLocalDataSource
) : MemoRepository {
    override suspend fun getMemoListByBookId(itemId: Int): Flow<List<MemoUiModel>> {
        return memoLocalDataSource.getMemoListByBookId(itemId)
            .map { entityList ->
                entityList.map {
                    MemoEntityMapper.entityToModel(it)
                }
            }
    }

    override suspend fun getMemoById(memoId: Int): MemoUiModel? {
        val entity = memoLocalDataSource.getMemoById(memoId)
        return entity?.let { MemoEntityMapper.entityToModel(it) }
    }

    override suspend fun deleteMemoById(memoId: Int) {
        memoLocalDataSource.deleteMemoById(memoId)
    }

    override suspend fun updateMemo(memoEntity: MemoEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun insertMemo(memoEntity: MemoEntity) {
        memoLocalDataSource.insert(memoEntity)
    }
}