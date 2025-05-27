package com.sy.odokcompose.core.data.di

import com.sy.odokcompose.core.data.repository.BookShelfRepository
import com.sy.odokcompose.core.data.repository.BookShelfRepositoryImpl
import com.sy.odokcompose.core.data.repository.SearchBookRepository
import com.sy.odokcompose.core.data.repository.SearchBookRepositoryImpl
import com.sy.odokcompose.core.data.local.datasource.BookLocalDataSource
import com.sy.odokcompose.core.data.local.datasource.BookLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    
    @Binds
    @Singleton
    fun bindSearchBookRepository(
        searchBookRepositoryImpl: SearchBookRepositoryImpl
    ): SearchBookRepository

    @Binds
    @Singleton
    fun bindBookShelfRepository(
        bookShelfRepositoryImpl: BookShelfRepositoryImpl
    ): BookShelfRepository

    @Binds
    @Singleton
    fun bindBookLocalDataSource(
        bookLocalDataSourceImpl: BookLocalDataSourceImpl
    ): BookLocalDataSource
} 