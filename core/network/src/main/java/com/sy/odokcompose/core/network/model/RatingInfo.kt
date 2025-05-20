package com.sy.odokcompose.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RatingInfo(
    @Json(name = "ratingScore") val ratingScore: Float?,
    @Json(name = "ratingCount") val ratingCount: Int?
)
