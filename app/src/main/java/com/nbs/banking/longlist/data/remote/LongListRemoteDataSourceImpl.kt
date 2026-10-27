package com.nbs.banking.longlist.data.remote

import com.nbs.banking.longlist.data.model.CursorResponse
import com.nbs.banking.longlist.data.model.DateRangeResponse
import com.nbs.banking.longlist.data.model.LimitOffsetResponse
import com.nbs.banking.longlist.data.model.PageSizeResponse
import com.nbs.banking.longlist.data.model.NumberResponse
import com.nbs.banking.longlist.data.model.TransactionResponse
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class LongListRemoteDataSourceImpl @Inject constructor(private val apiClient: LongListFakeApiClient) :
    LongListRemoteDataSource {

    override suspend fun getItemsWithPageSize(
        page: Int,
        size: Int
    ): PageSizeResponse<NumberResponse> {
        return apiClient.getItemsWithPageSize(page, size)
    }

    override suspend fun getItemsWithLimitOffset(
        limit: Int,
        offset: Int
    ): LimitOffsetResponse<NumberResponse> {
        return apiClient.getItemsWithLimitOffset(limit, offset)
    }

    override suspend fun getItemsWithCursor(
        cursor: String?,
        limit: Int
    ): CursorResponse<NumberResponse> {
        return apiClient.getItemsWithCursor(cursor, limit)
    }

    override suspend fun getTransactionsByDateRange(
        from: LocalDate,
        to: LocalDate,
        limit: Int,
        offset: Int
    ): DateRangeResponse<TransactionResponse> {
        return apiClient.getTransactionsByDateRange(from, to, limit, offset)
    }
}
