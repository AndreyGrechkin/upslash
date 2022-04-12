package com.jdefey.upsplash.data


import com.jdefey.upsplash.model.*
import retrofit2.http.*

interface UnsplashApi {
    @GET("photos")
    suspend fun getPhoto(
        @Query("per_page") pageElement: Int,
        @Query("page") page: Int
    ): List<Photo>

    @GET("search/photos")
    suspend fun getSearchPhoto(
        @Query("query") search: String,
        @Query("per_page") pageElement: Int,
        @Query("page") page: Int
    ): SearchResult<Photo>

    @GET("photos/{id}")
    suspend fun getPhotoId(@Path("id") photoId: String): PhotoDetail

    @POST("photos/{id}/like")
    suspend fun intoLikePhoto(@Path("id") photoId: String): LikePhoto

    @DELETE("photos/{id}/like")
    suspend fun outLikePhoto(@Path("id") photoId: String): LikePhoto

    @GET("photos/{id}/download")
    suspend fun downloadPhoto(@Path("id") photoId: String): PhotoLink

    @GET("collections")
    suspend fun getCollections(
        @Query("per_page") pageElement: Int,
        @Query("page") page: Int
    ): List<Collections>

    @GET("collections/{id}/photos")
    suspend fun getCollectionPhotoId(
        @Path("id") collectionId: String,
        @Query("per_page") pageElement: Int,
        @Query("page") page: Int
    ): List<Photo>

    @GET("collections/{id}")
    suspend fun getCollectionId(@Path("id") collectionId: String): CollectionPhoto

    @GET("me")
    suspend fun getUserProfile(): UserProfile

    @GET("users/{username}/likes")
    suspend fun getPhotoLikeByUser(
        @Path("username") username: String,
        @Query("per_page") pageElement: Int,
        @Query("page") page: Int
    ): List<PhotoLikeByUser>
}