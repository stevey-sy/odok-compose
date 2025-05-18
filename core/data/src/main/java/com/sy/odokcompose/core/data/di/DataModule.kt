package com.sy.odokcompose.core.data.di

import com.sy.odokcompose.core.data.repository.BookRepository
import com.sy.odokcompose.core.data.repository.BookRepositoryImpl
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
    fun bindBookRepository(
        bookRepositoryImpl: BookRepositoryImpl
    ): BookRepository
} 