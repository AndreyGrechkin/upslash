package com.jdefey.upsplash.model

import androidx.room.*
import com.jdefey.upsplash.database.Converter
import com.jdefey.upsplash.database.PhotoContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@TypeConverters(Converter::class)
@Entity(tableName = PhotoContract.TABLE_NAME)
@JsonClass(generateAdapter = true)
data class Photo(
    @PrimaryKey
    @ColumnInfo(name = PhotoContract.Columns.ID)
    val id: String,
    val user: Users,
    val urls: Urls,
    @ColumnInfo(name = PhotoContract.Columns.LIKES)
    val likes: String,
    @Json(name = "liked_by_user")
    @ColumnInfo(name = PhotoContract.Columns.LIKE_BY_USER)
    val likeByUser: Boolean
)

@JsonClass(generateAdapter = true)
data class Urls(
    @ColumnInfo(name = PhotoContract.Columns.SMALL_PHOTO)
    val small: String,
    val regular: String,
    val thumb: String
)

@JsonClass(generateAdapter = true)
data class Users(
    @ColumnInfo(name = PhotoContract.Columns.NICK_USER)
    val name: String,
    @ColumnInfo(name = PhotoContract.Columns.NAME_USER)
    val username: String,
    @Json(name = "profile_image")
    val profileImage: ProfileImage
)

@JsonClass(generateAdapter = true)
data class ProfileImage(
    @ColumnInfo(name = PhotoContract.Columns.PROFILE_IMAGE)
    val small: String
)





