package com.hjadal.portalis

import androidx.paging.PagingSource
import androidx.paging.PagingState
import coil.network.HttpException
import com.portalis.lib.Book
import com.portalis.lib.NetUtil
import com.portalis.lib.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class BookPager @Inject constructor(
    private val parser: RoyalRoadParser
) : PagingSource<Int, Book>() {
    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        // Retrofit calls that return the body type throw either IOException for network
        // failures, or HttpException for any non-2xx HTTP status codes. This code reports all
        // errors to the UI, but you can inspect/wrap the exceptions to provide more context.
        return try {
            // Key may be null during a refresh, if no explicit key is passed into Pager
            // construction. Use 0 as default, because our API is indexed started at index 0
            val pageNumber = params.key ?: 1

            // No networking on main tread :)
            withContext(Dispatchers.IO) {
                val response = loadBooks(pageNumber, parser.parser)
                // Since 1 is the lowest page number, return null to signify no more pages should
                // be loaded before it.
                val prevKey = if (pageNumber > 1) pageNumber - 1 else null

                // This API defines that it's out of data when a page returns empty. When out of
                // data, we return `null` to signify no more pages should be loaded
                val nextKey = if (response.isNotEmpty()) pageNumber + 1 else null
                LoadResult.Page(
                    data = response,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}


private fun loadBooks(page: Int, parser: Parser): List<Book> {
    val url = parser.getTopRatedPage(page)
    val result = NetUtil.get(url)
    return (result?.let { parser.parseOverview(it) } ?: emptyList())
}