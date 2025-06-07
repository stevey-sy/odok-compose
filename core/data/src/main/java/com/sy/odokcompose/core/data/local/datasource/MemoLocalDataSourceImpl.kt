package com.sy.odokcompose.core.data.local.datasource

import com.sy.odokcompose.core.database.MemoDao
import com.sy.odokcompose.core.database.entity.MemoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MemoLocalDataSourceImpl @Inject constructor(
    private val memoDao: MemoDao
) : MemoLocalDataSource {
    override suspend fun getMemoListByBookId(itemId: Int): Flow<List<MemoEntity>> {
       return memoDao.getMemosByBookId(itemId)
    }

    override suspend fun getMemoById(memoId: Int): MemoEntity? {
        return memoDao.getMemoById(memoId)
    }

    override suspend fun deleteMemoById(memoId: Int) {
        return memoDao.deleteMemoById(memoId)
    }

    override suspend fun updateMemo(memoEntity: MemoEntity) {
        memoDao.updateMemo(memoEntity)
    }

    override suspend fun insert(item: MemoEntity) {
        memoDao.insertMemo(item)
    }

    override suspend fun insertAll(items: List<MemoEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun update(item: MemoEntity) {
        memoDao.updateMemo(item)
    }

    override suspend fun delete(item: MemoEntity) {
        memoDao.deleteMemo(item)
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }

    override fun observeAll(): Flow<List<MemoEntity>> {
        TODO("Not yet implemented")
    }

    override fun observeById(id: Int): Flow<MemoEntity?> {
        TODO("Not yet implemented")
    }

}