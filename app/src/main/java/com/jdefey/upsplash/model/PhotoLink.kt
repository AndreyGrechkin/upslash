package com.jdefey.upsplash.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoLink(
    val url: String
)
