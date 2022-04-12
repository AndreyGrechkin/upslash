package com.jdefey.upsplash.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult<T>(
    val results: List<T>
)
