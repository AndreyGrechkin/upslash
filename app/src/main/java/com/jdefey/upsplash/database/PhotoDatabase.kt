package com.jdefey.upsplash.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jdefey.upsplash.model.Collections
import com.jdefey.upsplash.model.Photo

@Database(
    entities = [
        Photo::class,
        Collections::class
    ], version = PhotoDatabase.DB_VERSION
)
abstract class PhotoDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "photo-database"
    }
}
