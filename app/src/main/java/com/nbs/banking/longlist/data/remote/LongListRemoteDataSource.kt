package com.nbs.banking.longlist.data.remote

import com.nbs.banking.core.base.RemoteDataSource
import com.nbs.banking.longlist.data.model.CursorResponse
import com.nbs.banking.longlist.data.model.DateRangeResponse
import com.nbs.banking.longlist.data.model.LimitOffsetResponse
import com.nbs.banking.longlist.data.model.NumberResponse
import com.nbs.banking.longlist.data.model.PageSizeResponse
import com.nbs.banking.longlist.data.model.TransactionResponse
import kotlinx.datetime.LocalDate

interface LongListRemoteDataSource : RemoteDataSource {
    suspend fun getItemsWithPageSize(page: Int, size: Int): PageSizeResponse<NumberResponse>
    suspend fun getItemsWithLimitOffset(
        limit: Int,
        offset: Int
    ): LimitOffsetResponse<NumberResponse>

    suspend fun getItemsWithCursor(cursor: String?, limit: Int): CursorResponse<NumberResponse>
    suspend fun getTransactionsByDateRange(
        from: LocalDate,
        to: LocalDate,
        limit: Int,
        offset: Int,
    ): DateRangeResponse<TransactionResponse>
}
