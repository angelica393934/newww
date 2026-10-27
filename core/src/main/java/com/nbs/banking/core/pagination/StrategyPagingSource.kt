package com.nbs.banking.core.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nbs.banking.core.pagination.model.PagingResult
import com.nbs.banking.core.pagination.strategy.PaginationStrategy
import kotlin.coroutines.cancellation.CancellationException

class StrategyPagingSource<K : Any, T : Any>(
    private val strategy: PaginationStrategy<K>,
    private val fetch: suspend (key: K, loadSize: Int) -> PagingResult<T>,
) : PagingSource<K, T>() {

    override suspend fun load(params: LoadParams<K>): LoadResult<K, T> {
        val key = params.key ?: strategy.initialKey
        return try {
            val result = fetch(key, params.loadSize)
            LoadResult.Page(
                data = result.items,
                prevKey = strategy.prevKey(key, params.loadSize, result),
                nextKey = strategy.nextKey(key, params.loadSize, result),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<K, T>): K? =
        state.anchorPosition
            ?.let { state.closestPageToPosition(it) }
            ?.let { strategy.refreshKey(it.prevKey, it.nextKey, state.config.pageSize) }
}