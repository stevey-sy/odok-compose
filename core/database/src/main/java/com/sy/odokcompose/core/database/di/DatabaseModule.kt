package com.sy.odokcompose.core.database.di

import android.content.Context
import androidx.room.Room
import com.sy.odokcompose.core.database.BookDao
import com.sy.odokcompose.core.database.MemoDao
import com.sy.odokcompose.core.database.OdokDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideOdokDatabase(@ApplicationContext context: Context): OdokDatabase {
        return Room.databaseBuilder(
            context,
            OdokDatabase::class.java,
            "odok-database"
        ).fallbackToDestructiveMigration().build()
    }
    
    @Provides
    @Singleton
    fun provideBookDao(database: OdokDatabase): BookDao {
        return database.bookDao()
    }

    @Provides
    @Singleton
    fun provideMemoDao(database: OdokDatabase): MemoDao {
        return database.memoDao()
    }
} 