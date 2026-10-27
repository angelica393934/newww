package com.nbs.banking.core.pagination.strategy

import com.nbs.banking.core.pagination.model.PagingResult

data class Cursor(val value: String?)

// Cursor is the KEY
// Example: starts from cursor = null
// cursor = null,  items 1..10,  prevKey = null, nextKey = "abc" (first page)
// cursor = "abc", items 11..20, prevKey = null, nextKey = "def"
// cursor = "def", items 21..30, prevKey = null, nextKey = "ghi"
// cursor = "ghi", items 31..36, prevKey = null, nextKey = null (last page)
class CursorStrategy : PaginationStrategy<Cursor> {
    override val initialKey = Cursor(null)
    override fun prevKey(key: Cursor, loadSize: Int, result: PagingResult<*>) =
        result.prevCursor?.let(::Cursor)

    override fun nextKey(key: Cursor, loadSize: Int, result: PagingResult<*>) =
        result.nextCursor?.let(::Cursor)

    override fun refreshKey(prevKey: Cursor?, nextKey: Cursor?, pageSize: Int): Cursor? = null
}