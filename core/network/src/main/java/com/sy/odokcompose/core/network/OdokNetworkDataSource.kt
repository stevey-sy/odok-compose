package com.sy.odokcompose.core.network

import com.sy.odokcompose.core.network.model.AladinBookItem
import com.sy.odokcompose.core.network.model.AladinBookSearchResponse

interface OdokNetworkDataSource {
    suspend fun getSearchedBooks(query: String, start: Int, maxResults: Int): AladinBookSearchResponse
}