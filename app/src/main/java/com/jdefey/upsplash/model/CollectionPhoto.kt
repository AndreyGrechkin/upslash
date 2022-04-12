package com.jdefey.upsplash.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CollectionPhoto(
    val id: String,
    val title: String?,
    @Json(name = "total_photos")
    val totalPhoto: Int?,
    val description: String?,
    val user: UserNameCollection?,
    @Json(name = "cover_photo")
    val coverPhoto: CoverIdCollection?,
    val tags: List<TagsCollection>?
)

@JsonClass(generateAdapter = true)
data class TagsCollection(
    val title: String?
)

@JsonClass(generateAdapter = true)
data class CoverIdCollection(
    val urls: UrlsCover?
)

@JsonClass(generateAdapter = true)
data class UrlsCover(
    val regular: String?
)

@JsonClass(generateAdapter = true)
data class UserNameCollection(
    val name: String?
)

