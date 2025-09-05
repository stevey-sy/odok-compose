package com.sy.odokcompose.core.data.di

import com.sy.odokcompose.core.data.repository.BookShelfRepository
import com.sy.odokcompose.core.data.repository.BookShelfRepositoryImpl
import com.sy.odokcompose.core.data.repository.SearchBookRepository
import com.sy.odokcompose.core.data.repository.SearchBookRepositoryImpl
import com.sy.odokcompose.core.data.local.datasource.BookLocalDataSource
import com.sy.odokcompose.core.data.local.datasource.BookLocalDataSourceImpl
import com.sy.odokcompose.core.data.local.datasource.MemoLocalDataSource
import com.sy.odokcompose.core.data.local.datasource.MemoLocalDataSourceImpl
import com.sy.odokcompose.core.data.repository.AuthRepository
import com.sy.odokcompose.core.data.repository.AuthRepositoryImpl
import com.sy.odokcompose.core.data.repository.MemoRepository
import com.sy.odokcompose.core.data.repository.MemoRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.sy.odokcompose.core.data.BuildConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.status.SessionSource.Storage
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

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

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }

        @Provides
        @Singleton
        fun provideSupabaseClient(): SupabaseClient {
            return createSupabaseClient(
                supabaseUrl = BuildConfig.SUPABASE_URL,
                supabaseKey = BuildConfig.SUPABASE_ANON_KEY
            ) {
                install(Postgrest)
                install(Auth)
//                install(Storage)
            }
        }
    }

} 