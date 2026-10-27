package com.nbs.banking.core.pagination.strategy

import com.nbs.banking.core.pagination.model.PagingResult
import com.nbs.banking.core.pagination.isLastPage

//page is the KEY
//Example: pageSize = 10 , starts from page = 1
//page = 1, items 1..10, prev key = null, nextKey = 2 (first page)
//page = 2, items 11..20, prev key = 1, nextKey = 3
//page = 3, items 21..30, prev key = 2, nextKey = 4
//page = 4, items 31..36, prevKey = 3, nextKey = null (Last page)
class PageSizeStrategy(
    private val startPage: Int = 1,
    private val refreshPolicy: PagingRefreshPolicy
) : PaginationStrategy<Int> {
    override val initialKey = startPage
    override fun prevKey(key: Int, loadSize: Int, result: PagingResult<*>) =
        if (key <= startPage) null else key - 1

    override fun nextKey(key: Int, loadSize: Int, result: PagingResult<*>) =
        if (result.isLastPage(loadSize)) null else key + 1

    override fun refreshKey(prevKey: Int?, nextKey: Int?, pageSize: Int) =
        when (refreshPolicy) {
            PagingRefreshPolicy.RESET_TO_INITIAL -> initialKey
            else -> prevKey?.plus(1) ?: nextKey?.minus(1)
        }
}