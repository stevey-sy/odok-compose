package com.sy.odokcompose.core.data.di

import com.sy.odokcompose.core.data.repository.BookShelfRepository
import com.sy.odokcompose.core.data.repository.BookShelfRepositoryImpl
import com.sy.odokcompose.core.data.repository.SearchBookRepository
import com.sy.odokcompose.core.data.repository.SearchBookRepositoryImpl
import com.sy.odokcompose.core.data.local.datasource.BookLocalDataSource
import com.sy.odokcompose.core.data.local.datasource.BookLocalDataSourceImpl
import com.sy.odokcompose.core.data.local.datasource.MemoLocalDataSource
import com.sy.odokcompose.core.data.local.datasource.MemoLocalDataSourceImpl
import com.sy.odokcompose.core.data.repository.MemoRepository
import com.sy.odokcompose.core.data.repository.MemoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    
    @Binds
    fun bindSearchBookRepository(
        searchBookRepositoryImpl: SearchBookRepositoryImpl
    ): SearchBookRepository

    @Binds
    fun bindBookShelfRepository(
        bookShelfRepositoryImpl: BookShelfRepositoryImpl
    ): BookShelfRepository

    @Binds
    fun bindBookLocalDataSource(
        bookLocalDataSourceImpl: BookLocalDataSourceImpl
    ): BookLocalDataSource

    @Binds
    fun bindMemoLocalDataSource(
        memoLocalDataSourceImpl: MemoLocalDataSourceImpl
    ): MemoLocalDataSource

    @Binds
    fun bindMemoRepository(
        memoRepositoryImpl: MemoRepositoryImpl
    ): MemoRepository
} 