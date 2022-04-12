package com.jdefey.upsplash.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jdefey.upsplash.database.Database
import com.jdefey.upsplash.model.Collections

typealias CollectionPageLoader = suspend (pageElement: Int, page: Int) -> List<Collections>

class CollectionPagingSource(
    private val loader: CollectionPageLoader,
    private val pageSize: Int
) : PagingSource<Int, Collections>() {
    override fun getRefreshKey(state: PagingState<Int, Collections>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Collections> {
        val pageIndex = params.key ?: 0

        return try {
            val collection = loader.invoke(pageIndex, params.loadSize)
            Database.instance.photoDao().insertCollection(collection)
            return LoadResult.Page(
                data = collection,
                prevKey = if (pageIndex == 0) null else pageIndex - 1,
                nextKey = if (collection.size == params.loadSize) pageIndex + (params.loadSize / pageSize) else null
            )
        } catch (e: Exception) {
            LoadResult.Error(throwable = e)
        }
    }
}