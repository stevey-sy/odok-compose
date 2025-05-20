package com.sy.odokcompose.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubInfo(
    @Json(name = "itemPage") val itemPage: Int = 0,
    @Json(name = "ratingInfo") val ratingInfo: RatingInfo?, // 평점 정보
)
