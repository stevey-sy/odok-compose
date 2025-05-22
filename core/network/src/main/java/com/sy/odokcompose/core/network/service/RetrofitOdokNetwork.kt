package com.sy.odokcompose.core.network.service

import com.sy.odokcompose.core.network.OdokNetworkDataSource
import com.sy.odokcompose.core.network.model.AladinBookSearchResponse
import com.sy.odokcompose.core.network.BuildConfig
import com.sy.odokcompose.core.network.model.AladinBookDetailResponse
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

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

    override suspend fun getBookDetail(itemId: String): AladinBookDetailResponse {
        return apiService.getBookDetail(
            itemId = itemId,
        )
    }
}