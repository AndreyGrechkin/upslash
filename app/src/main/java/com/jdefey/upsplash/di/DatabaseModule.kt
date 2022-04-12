package com.jdefey.upsplash.di

import android.app.Application
import androidx.room.Room
import com.jdefey.upsplash.database.PhotoDao
import com.jdefey.upsplash.database.PhotoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class DatabaseModule {

    @Provides
    fun providesDatabase(application: Application): PhotoDatabase {
        return Room.databaseBuilder(
            application,
            PhotoDatabase::class.java,
            PhotoDatabase.DB_NAME
        )
            .build()
    }

    @Provides
    fun providesPhotoDao(db: PhotoDatabase): PhotoDao {
        return db.photoDao()
    }
}