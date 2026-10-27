package com.nbs.banking.core.pagination.strategy

import com.nbs.banking.core.pagination.model.PagingResult
import com.nbs.banking.core.pagination.isLastPage

//offset is the KEY
//Example: Limit = 10, starts from offset 0
//offset = 0, items 1..10, prev key = null, nextKey = 10 (first page)
//offset = 10, items 11..20, prev key = 0, nextKey = 20
//offset = 20, items 21..30, prev key = 10, nextKey = 30
//offset = 30, items 31..36, prevKey = 20, nextKey = null (Last page)
class OffsetLimitStrategy(private val refreshPolicy: PagingRefreshPolicy) :
    PaginationStrategy<Int> {
    override val initialKey = 0
    override fun prevKey(key: Int, loadSize: Int, result: PagingResult<*>) =
        if (key == 0) null else (key - loadSize).coerceAtLeast(0)

    override fun nextKey(key: Int, loadSize: Int, result: PagingResult<*>) =
        if (result.isLastPage(loadSize)) null else key + loadSize

    override fun refreshKey(prevKey: Int?, nextKey: Int?, pageSize: Int) =
        when (refreshPolicy) {
            PagingRefreshPolicy.RESET_TO_INITIAL -> initialKey
            else -> prevKey?.plus(pageSize) ?: nextKey?.minus(pageSize)?.coerceAtLeast(0)
        }
}