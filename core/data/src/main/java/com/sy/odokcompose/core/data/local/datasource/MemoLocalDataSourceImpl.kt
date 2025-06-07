package com.sy.odokcompose.core.data.local.datasource

import com.sy.odokcompose.core.database.MemoDao
import com.sy.odokcompose.core.database.entity.MemoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MemoLocalDataSourceImpl @Inject constructor(
    private val memoDao: MemoDao
) : MemoLocalDataSource {

    override suspend fun insert(item: MemoEntity) {
        memoDao.insertMemo(item)
    }

    override suspend fun insertAll(items: List<MemoEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun update(item: MemoEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(item: MemoEntity) {
        TODO("Not yet implemented")
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