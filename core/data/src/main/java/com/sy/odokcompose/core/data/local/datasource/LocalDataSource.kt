package com.sy.odokcompose.core.data.local.datasource

import kotlinx.coroutines.flow.Flow

interface LocalDataSource<T> {
    suspend fun insert(item: T)
    suspend fun insertAll(items: List<T>)
    suspend fun update(item: T)
    suspend fun delete(item: T)
    suspend fun deleteAll()
    fun observeAll(): Flow<List<T>>
    fun observeById(id: Int): Flow<T?>
}