package com.jdefey.upsplash.database

import androidx.room.*
import com.jdefey.upsplash.model.Collections
import com.jdefey.upsplash.model.Photo

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: List<Photo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: List<Collections>)

    @Query("SELECT * FROM ${CollectionContract.TABLE_NAME}")
    suspend fun getAllCollection(): List<Collections>

    @Query("SELECT * FROM ${PhotoContract.TABLE_NAME}")
    suspend fun getAllPhoto(): List<Photo>

    @Query("DELETE FROM ${PhotoContract.TABLE_NAME}")
    suspend fun getDeletePhoto()

    @Query("DELETE FROM ${CollectionContract.TABLE_NAME}")
    suspend fun getDeleteCollection()

}