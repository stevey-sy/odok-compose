package com.sy.odokcompose.core.network.retrofit

import androidx.core.os.trace
import com.sy.odokcompose.core.network.OdokNetworkDataSource
import com.sy.odokcompose.core.network.model.AladinBookItem
import com.sy.odokcompose.core.network.model.AladinBookSearchResponse
import com.sy.odokcompose.core.network.BuildConfig
import dagger.Lazy
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton


private interface RetrofitOdokNetworkApi {
    @GET(value = "ItemSearch.aspx")
    suspend fun getSearchedBooks(
        @Query("ttbkey") ttbKey: String = BuildConfig.ALADIN_TTB_KEY,
        @Query("Query") query: String,
        @Query("QueryType") queryType: String = "Keyword", // 기본값 설정
        @Query("SearchTarget") searchTarget: String = "Book", // 기본값 설정
        @Query("Start") start: Int = 1,
        @Query("MaxResults") maxResults: Int = 10,
        @Query("Sort") sort: String = "Accuracy",
        @Query("Cover") cover: String = "Big",
        @Query("Output") output: String = "JS",
        @Query("Version") version: String = "20131101"
    ): AladinBookSearchResponse
}

@Singleton
class RetrofitOdokNetwork @Inject constructor(
    moshi: Moshi,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : OdokNetworkDataSource {

    private val networkApi = trace("RetrofitOdokNetwork") {
        Retrofit.Builder()
            .baseUrl("https://www.aladin.co.kr/ttb/api/")
            .callFactory{
                okhttpCallFactory.get().newCall(it)
            }
            .addConverterFactory(
                MoshiConverterFactory.create(moshi)
            )
            .build()
            .create(RetrofitOdokNetworkApi::class.java)
    }

    override suspend fun getSearchedBooks(
        query: String,
        start: Int,
        maxResults: Int
    ): AladinBookSearchResponse {
        return networkApi.getSearchedBooks(
            query = query, 
            start = start, 
            maxResults = maxResults
        )
    }

}