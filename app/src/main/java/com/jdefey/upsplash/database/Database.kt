package com.jdefey.upsplash.database

import android.content.Context
import androidx.room.Room

object Database {
    lateinit var instance: PhotoDatabase
        private set

    fun init(context: Context) {
        instance =
            Room.databaseBuilder(context, PhotoDatabase::class.java, PhotoDatabase.DB_NAME)
                .build()
    }
}