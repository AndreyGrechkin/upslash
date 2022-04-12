package com.jdefey.upsplash.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jdefey.upsplash.model.Photo
import com.jdefey.upsplash.database.Database

typealias PhotoPageLoader = suspend (pageElement: Int, page: Int) -> List<Photo>

class PhotoPagingSource(
    private val loader: PhotoPageLoader,
    private val pageSize: Int
) : PagingSource<Int, Photo>() {
    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageIndex = params.key ?: 0

        return try {
            val photos = loader.invoke(pageIndex, params.loadSize)
            Database.instance.photoDao().insertPhoto(photos)
            return LoadResult.Page(
                data = photos,
                prevKey = if (pageIndex == 0) null else pageIndex - 1,
                nextKey = if (photos.size == params.loadSize) pageIndex + (params.loadSize / pageSize) else null
            )
        } catch (e: Exception) {
            LoadResult.Error(throwable = e)
        }
    }
}