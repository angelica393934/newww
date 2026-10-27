package com.nbs.banking.core.pagination.strategy

import com.nbs.banking.core.pagination.model.PagingResult

interface PaginationStrategy<K : Any> {
    val initialKey: K
    fun prevKey(key: K, loadSize: Int, result: PagingResult<*>): K?
    fun nextKey(key: K, loadSize: Int, result: PagingResult<*>): K?
    fun refreshKey(prevKey: K?, nextKey: K?, pageSize: Int): K?
}