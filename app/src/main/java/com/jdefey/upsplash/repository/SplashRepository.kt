package com.jdefey.upsplash.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jdefey.upsplash.data.Networking
import com.jdefey.upsplash.database.PhotoDao
import com.jdefey.upsplash.model.*
import com.jdefey.upsplash.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SplashRepository @Inject constructor(
    private val photoDao: PhotoDao
) : PhotoRepository {

    override suspend fun getPhotoById(photoId: String): PhotoDetail {
        return withContext(Dispatchers.IO) {
            Networking.unsplashApi.getPhotoId(photoId)
        }
    }

    override suspend fun setPhotoLike(photoId: String): LikePhoto {
        return withContext(Dispatchers.IO) {
            Networking.unsplashApi.intoLikePhoto(photoId)
        }
    }

    override suspend fun setCollectionId(collectionId: String): CollectionPhoto {
        return withContext(Dispatchers.IO) {
            Networking.unsplashApi.getCollectionId(collectionId)
        }
    }

    override suspend fun setPhotoLikeDelete(photoId: String): LikePhoto {
        return withContext(Dispatchers.IO) {
            Networking.unsplashApi.outLikePhoto(photoId)
        }
    }

    override suspend fun downloadPhoto(photoId: String): PhotoLink {
        return withContext(Dispatchers.IO) {
            Networking.unsplashApi.downloadPhoto(photoId)
        }
    }

    override suspend fun getUserProfile(): UserProfile {
        return withContext(Dispatchers.IO) {
            Networking.unsplashApi.getUserProfile()
        }
    }

    override fun getPagedPhotos(search: String): Flow<PagingData<Photo>> {
        val loader: PhotoPageLoader = { pageIndex, pageSize ->
            getPhoto(pageIndex, pageSize, search)
        }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotoPagingSource(loader, PAGE_SIZE) }
        ).flow
    }

    override fun getPagedCollection(): Flow<PagingData<Collections>> {
        val loader: CollectionPageLoader = { pageIndex, pageSize ->
            getCollection(pageIndex, pageSize)
        }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CollectionPagingSource(loader, PAGE_SIZE) }
        ).flow
    }

    override suspend fun getCollectionPhoto(collectionId: String): Flow<PagingData<Photo>> {
        val loader: PhotoPageLoader = { pageIndex, pageSize ->
            getPhotoCollection(pageIndex, pageSize, collectionId)
        }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotoPagingSource(loader, PAGE_SIZE) }
        ).flow
    }

    override suspend fun getPhotoLikeByUser(username: String): Flow<PagingData<PhotoLikeByUser>> {
        val loader: PhotoLikeUserPageLoader = { pageIndex, pageSize ->
            getPhotoLike(pageIndex, pageSize, username)
        }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotoLikeUserPagingSource(loader, PAGE_SIZE) }
        ).flow
    }

    private suspend fun getPhoto(pageIndex: Int, pageSize: Int, search: String): List<Photo> =
        withContext(Dispatchers.IO) {
            val offset = pageIndex * pageSize
            val list = if (search == "") {
                try {
                    Networking.unsplashApi.getPhoto(pageSize, offset)
                } catch (e: Throwable) {
                    photoDao.getAllPhoto()
                }
            } else {
                try {
                    Networking.unsplashApi.getSearchPhoto(search, pageSize, offset).results
                } catch (e: Throwable) {
                    photoDao.getAllPhoto()
                }
            }
            return@withContext list
        }

    private suspend fun getPhotoCollection(
        pageIndex: Int,
        pageSize: Int,
        collectionId: String
    ): List<Photo> =
        withContext(Dispatchers.IO) {
            val offset = pageIndex * pageSize
            val list =
                try {
                    Networking.unsplashApi.getCollectionPhotoId(collectionId, pageSize, offset)
                } catch (e: Throwable) {
                    photoDao.getAllPhoto()
                }
            return@withContext list
        }

    private suspend fun getCollection(pageIndex: Int, pageSize: Int): List<Collections> =
        withContext(Dispatchers.IO) {
            val offset = pageIndex * pageSize
            val list =
                try {
                    Networking.unsplashApi.getCollections(pageSize, offset)
                } catch (e: Throwable) {
                    photoDao.getAllCollection()
                }
            return@withContext list
        }

    private suspend fun getPhotoLike(
        pageIndex: Int,
        pageSize: Int,
        username: String
    ): List<PhotoLikeByUser> =
        withContext(Dispatchers.IO) {
            val offset = pageIndex * pageSize
            return@withContext Networking.unsplashApi.getPhotoLikeByUser(username, pageSize, offset)
        }

    override suspend fun deletePhotoDb() {
        photoDao.getDeletePhoto()
        photoDao.getDeleteCollection()
    }

    private companion object {
        const val PAGE_SIZE = 10
    }
}