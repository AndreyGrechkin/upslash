package com.jdefey.upsplash.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.jdefey.upsplash.database.CollectionContract
import com.jdefey.upsplash.database.Converter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@TypeConverters(Converter::class)
@Entity(tableName = CollectionContract.TABLE_NAME)
@JsonClass(generateAdapter = true)
data class Collections(
    @PrimaryKey
    @ColumnInfo(name = CollectionContract.Columns.ID)
    val id: String,
    @ColumnInfo(name = CollectionContract.Columns.TITLE)
    val title: String,
    @Json(name = "total_photos")
    @ColumnInfo(name = CollectionContract.Columns.TOTAL_PHOTO)
    val totalPhoto: Int,
    val user: UserCollection,
    @Json(name = "cover_photo")
    val coverPhoto: CoverPhotoCollection
)

@JsonClass(generateAdapter = true)
data class UserCollection(
    @ColumnInfo(name = CollectionContract.Columns.NAME_USER)
    val name: String?,
    @Json(name = "profile_image")
    val profileImage: ProfileImageCollection
)

@JsonClass(generateAdapter = true)
data class ProfileImageCollection(
    @ColumnInfo(name = CollectionContract.Columns.PROFILE_IMAGE)
    val small: String?
)

@JsonClass(generateAdapter = true)
data class CoverPhotoCollection(
    val urls: UrlsCollection
)

@JsonClass(generateAdapter = true)
data class UrlsCollection(
    @ColumnInfo(name = CollectionContract.Columns.COVER_PHOTO)
    val regular: String
)