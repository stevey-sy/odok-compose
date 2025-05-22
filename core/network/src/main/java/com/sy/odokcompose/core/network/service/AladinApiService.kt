package com.sy.odokcompose.core.network.service

import com.sy.odokcompose.core.network.BuildConfig
import com.sy.odokcompose.core.network.model.AladinBookDetailResponse
import com.sy.odokcompose.core.network.model.AladinBookSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 알라딘 API 서비스 인터페이스
 */
interface AladinApiService {
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

    @GET("ItemLookUp.aspx")
    suspend fun getBookDetail(
        @Query("ttbkey") ttbKey: String = BuildConfig.ALADIN_TTB_KEY,
        @Query("itemIdType") itemIdType: String = "ISBN",
        @Query("ItemId") itemId: String, // 기본값 설정
        @Query("Cover") cover: String = "Big",
        @Query("Output") output: String = "JS",
        @Query("Version") version: String = "20131101"
    ): AladinBookDetailResponse
    
    companion object {
        const val BASE_URL = "https://www.aladin.co.kr/ttb/api/"
    }
} 