package com.jdefey.upsplash.repository

import androidx.paging.PagingData
import com.jdefey.upsplash.model.*
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {

    suspend fun getPhotoById(photoId: String): PhotoDetail

    suspend fun setPhotoLike(photoId: String): LikePhoto

    suspend fun setPhotoLikeDelete(photoId: String): LikePhoto

    fun getPagedPhotos(search: String): Flow<PagingData<Photo>>

    suspend fun downloadPhoto(photoId: String): PhotoLink

    fun getPagedCollection(): Flow<PagingData<Collections>>

    suspend fun getCollectionPhoto(collectionId: String): Flow<PagingData<Photo>>

    suspend fun setCollectionId(collectionId: String): CollectionPhoto

    suspend fun getUserProfile(): UserProfile

    suspend fun getPhotoLikeByUser(username: String): Flow<PagingData<PhotoLikeByUser>>

    suspend fun deletePhotoDb()
}