package com.jdefey.upsplash.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoLikeByUser(
    val id: String,
    val urls: UrlsPhotoLike,
    val likes: Int,
    @Json(name = "liked_by_user")
    val likeByUser: Boolean,
    val user: UserLikePhoto?,
)

@JsonClass(generateAdapter = true)
data class UserLikePhoto(
    val name: String?,
    val username: String,
    @Json(name = "profile_image")
    val profileImage: AvatarPhotoLike?
)

@JsonClass(generateAdapter = true)
data class AvatarPhotoLike(
    val small: String?
)

@JsonClass(generateAdapter = true)
data class UrlsPhotoLike(
    val regular: String
)