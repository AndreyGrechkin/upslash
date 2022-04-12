package com.jdefey.upsplash.database

import androidx.room.TypeConverter
import com.jdefey.upsplash.model.CoverPhotoCollection
import com.jdefey.upsplash.model.Urls
import com.jdefey.upsplash.model.UserCollection
import com.jdefey.upsplash.model.Users
import com.squareup.moshi.Moshi

class Converter {
    @TypeConverter
    fun stringToUser(input: String?): Users? =
        input?.let { Moshi.Builder().build().adapter(Users::class.java).fromJson(it) }

    @TypeConverter
    fun userToString(input: Users): String? =
        Moshi.Builder().build().adapter(Users::class.java).toJson(input)

    @TypeConverter
    fun stringToUrls(input: String?): Urls? =
        input?.let { Moshi.Builder().build().adapter(Urls::class.java).fromJson(it) }

    @TypeConverter
    fun urlsToString(input: Urls): String? =
        Moshi.Builder().build().adapter(Urls::class.java).toJson(input)

    @TypeConverter
    fun stringToUserCollection(input: String?): UserCollection? =
        input?.let { Moshi.Builder().build().adapter(UserCollection::class.java).fromJson(it) }

    @TypeConverter
    fun userCollectionToString(input: UserCollection): String? =
        Moshi.Builder().build().adapter(UserCollection::class.java).toJson(input)

    @TypeConverter
    fun stringToCoverPhotoCollection(input: String?): CoverPhotoCollection? =
        input?.let {
            Moshi.Builder().build().adapter(CoverPhotoCollection::class.java).fromJson(it)
        }

    @TypeConverter
    fun coverPhotoCollectionToString(input: CoverPhotoCollection): String? =
        Moshi.Builder().build().adapter(CoverPhotoCollection::class.java).toJson(input)
}