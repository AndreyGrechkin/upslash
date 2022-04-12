package com.jdefey.upsplash.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoDetail(
    val id: String,
    val downloads: Int?,
    val likes: Int?,
    @Json(name = "liked_by_user")
    val likeByUser: Boolean,
    val exif: Exif?,
    val location: LocationPhoto?,
    val tags: List<Tags>?,
    val urls: UrlsPhoto,
    val user: User?
)

@JsonClass(generateAdapter = true)
data class Exif(
    val make: String?,
    val model: String?,
    @Json(name = "exposure_time")
    val exposure: String?,
    val aperture: String?,
    @Json(name = "focal_length")
    val focalLength: String?,
    val iso: Int?
)

@JsonClass(generateAdapter = true)
data class LocationPhoto(
    val city: String?,
    val country: String?,
    val position: CoordinatePhoto?
)

@JsonClass(generateAdapter = true)
data class CoordinatePhoto(
    val latitude: Double?,
    val longitude: Double?
)

@JsonClass(generateAdapter = true)
data class Tags(
    val title: String?
)

@JsonClass(generateAdapter = true)
data class UrlsPhoto(
    val small: String,
    val raw: String,
    val regular: String
)

@JsonClass(generateAdapter = true)
data class User(
    val name: String?,
    val bio: String?,
    @Json(name = "profile_image")
    val profileImage: ProfileImageUser?,
)

@JsonClass(generateAdapter = true)
data class ProfileImageUser(
    val small: String?
)