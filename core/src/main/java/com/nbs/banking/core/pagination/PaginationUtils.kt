package com.nbs.banking.core.pagination

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.nbs.banking.core.pagination.model.PagingResult
import com.nbs.banking.core.pagination.strategy.PaginationStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun PagingResult<*>.isLastPage(loadSize: Int): Boolean =
    items.size < loadSize

fun <K : Any, T : Any, R : Any> paginatedFlow(
    strategy: PaginationStrategy<K>,
    pagingConfig: PagingConfig,
    fetch: suspend (key: K, loadSize: Int) -> PagingResult<T>,
    mapper: (T) -> R,
): Flow<PagingData<R>> {
    require(pagingConfig.initialLoadSize == pagingConfig.pageSize) {
        "initialLoadSize (${pagingConfig.initialLoadSize}) must equal pageSize (${pagingConfig.pageSize})"
    }
    return Pager(
        config = pagingConfig,
        pagingSourceFactory = { StrategyPagingSource(strategy, fetch) },
    ).flow.map { it.map(mapper) }
}