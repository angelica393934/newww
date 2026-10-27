package com.nbs.banking.core.pagination.model

data class PagingResult<T>(
    val items: List<T>,
    val nextCursor: String? = null,
    val prevCursor: String? = null
)
