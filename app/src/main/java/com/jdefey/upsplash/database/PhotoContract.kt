package com.jdefey.upsplash.database

object PhotoContract {
    const val TABLE_NAME = "photo_db"
    object Columns {
        const val ID = "id"
        const val LIKES = "likes"
        const val LIKE_BY_USER = "likeByUser"
        const val SMALL_PHOTO = "small"
        const val NAME_USER = "username"
        const val NICK_USER = "name"
        const val PROFILE_IMAGE = "profileImage"
    }
}