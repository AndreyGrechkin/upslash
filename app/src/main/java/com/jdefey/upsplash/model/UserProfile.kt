package com.jdefey.upsplash.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfile(
    val id: String,
    val name: String?,
    val bio: String?,
    val location: String?,
    val username: String,
    @Json(name = "profile_image")
    val avatar: UserAvatar?,
    val email: String?,
    val downloads: Int?,
    @Json(name = "total_likes")
    val totalLike: Int?
)

@JsonClass(generateAdapter = true)
data class UserAvatar(
    val large: String?
)