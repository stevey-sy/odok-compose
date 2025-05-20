package com.sy.odokcompose.core.network.service

import com.sy.odokcompose.core.network.OdokNetworkDataSource
import com.sy.odokcompose.core.network.model.AladinBookSearchResponse
import com.sy.odokcompose.core.network.BuildConfig
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
    private val retrofitClient: RetrofitClient
) : OdokNetworkDataSource {

    private val apiService: AladinApiService by lazy {
        retrofitClient.createService(
            AladinApiService::class.java,
            AladinApiService.BASE_URL
        )
    }

    override suspend fun getSearchedBooks(
        query: String,
        start: Int,
        maxResults: Int
    ): AladinBookSearchResponse {
        return apiService.getSearchedBooks(
            query = query, 
            start = start, 
            maxResults = maxResults
        )
    }

}