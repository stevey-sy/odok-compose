package com.sy.odokcompose.core.network.model

data class AladinBookItem(
    val title: String,
    val link: String,
    val author: String,
    val pubDate: String,
    val description: String?,
    val isbn: String,
    val isbn13: String,
    val pricesales: Int,
    val pricestandard: Int,
    val mallType: String,
    val stockstatus: String?,
    val mileage: Int,
    val cover: String,
    val publisher: String,
    val salesPoint: Int,
    val adult: Boolean,
    val fixedPrice: Boolean,
    val customerReviewRank: Float,
    val bestDuration: String?,
    val bestRank: Int?,
    val subInfo: SubInfo?
)
