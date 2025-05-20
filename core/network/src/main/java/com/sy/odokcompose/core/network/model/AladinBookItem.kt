package com.sy.odokcompose.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AladinBookItem(
    @Json(name = "title") val title: String,
    @Json(name = "link") val link: String,
    @Json(name = "author") val author: String,
    @Json(name = "pubDate") val pubDate: String,
    @Json(name = "description") val description: String?,
    @Json(name = "isbn") val isbn: String,
    @Json(name = "isbn13") val isbn13: String,
    @Json(name = "pricesales") val pricesales: Int,
    @Json(name = "pricestandard") val pricestandard: Int,
    @Json(name = "mallType") val mallType: String,
    @Json(name = "stockstatus") val stockstatus: String?,
    @Json(name = "mileage") val mileage: Int,
    @Json(name = "cover") val cover: String,
    @Json(name = "publisher") val publisher: String,
    @Json(name = "salesPoint") val salesPoint: Int,
    @Json(name = "adult") val adult: Boolean,
    @Json(name = "fixedPrice") val fixedPrice: Boolean,
    @Json(name = "customerReviewRank") val customerReviewRank: Float,
    @Json(name = "bestDuration") val bestDuration: String?,
    @Json(name = "bestRank") val bestRank: Int?,
    @Json(name = "subInfo") val subInfo: SubInfo?
)
