package com.jdefey.upsplash.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LikePhoto(
    val photo: PhotoInfo
)

@JsonClass(generateAdapter = true)
data class PhotoInfo(
    val id: String,
    @Json(name = "liked_by_user")
    val liked: Boolean,
    val likes: Int
)

