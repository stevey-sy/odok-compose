package com.sy.odokcompose.core.network.di

import com.sy.odokcompose.core.network.OdokNetworkDataSource
import com.sy.odokcompose.core.network.retrofit.RetrofitOdokNetwork
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.sy.odokcompose.core.network.interceptor.HttpRequestInterceptor
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NetworkModule {

//    @Provides
//    @Singleton
//    fun provideOkHttpClient(): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(HttpRequestInterceptor())
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
//        return Retrofit.Builder()
//            .client(okHttpClient)
//            .baseUrl("https://www.aladin.co.kr/ttb/api/")
//            .addConverterFactory(MoshiConverterFactory.create())
//            .build()
//    }


    @Binds
    @Singleton
    fun bindsOdokNetworkDataSource(
        retrofitOdokNetwork: RetrofitOdokNetwork
    ): OdokNetworkDataSource
    
    companion object {
        @Provides
        @Singleton
        fun providesMoshi(): Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        
        @Provides
        @Singleton
        fun providesOkHttpCallFactory(): Call.Factory = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
} 