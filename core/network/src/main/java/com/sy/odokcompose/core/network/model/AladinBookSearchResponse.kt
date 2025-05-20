package com.sy.odokcompose.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AladinBookSearchResponse(
    @Json(name = "version") val version: String,
    @Json(name = "title") val title: String,
    @Json(name = "link") val link: String,
    @Json(name = "pubDate") val pubDate: String,
    @Json(name = "totalResults") val totalResults: Int,
    @Json(name = "startIndex") val startIndex: Int,
    @Json(name = "itemsPerPage") val itemsPerPage: Int,
    @Json(name = "query") val query: String,
    @Json(name = "searchCategoryId") val searchCategoryId: Int?,
    @Json(name = "searchCategoryName") val searchCategoryName: String?,
    @Json(name = "item") val item: List<AladinBookItem> // 상품 목록
)
