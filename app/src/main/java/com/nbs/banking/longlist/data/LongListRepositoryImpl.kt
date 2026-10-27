package com.nbs.banking.longlist.data

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nbs.banking.core.pagination.strategy.OffsetLimitStrategy
import com.nbs.banking.core.pagination.model.PagingResult
import com.nbs.banking.core.pagination.paginatedFlow
import com.nbs.banking.core.pagination.strategy.CursorStrategy
import com.nbs.banking.core.pagination.strategy.DateWindowStrategy
import com.nbs.banking.core.pagination.strategy.PageSizeStrategy
import com.nbs.banking.core.pagination.strategy.PagingRefreshPolicy
import com.nbs.banking.longlist.data.mapper.toDomain
import com.nbs.banking.longlist.data.model.NumberResponse
import com.nbs.banking.longlist.data.model.TransactionResponse
import com.nbs.banking.longlist.data.remote.LongListRemoteDataSource
import com.nbs.banking.longlist.domain.LongListRepository
import com.nbs.banking.longlist.domain.model.NumberItem
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import timber.log.Timber
import javax.inject.Inject

class LongListRepositoryImpl @Inject constructor(
    override val remoteDataSource: LongListRemoteDataSource,
) : LongListRepository {
    override fun getLongListOffsetLimit(): Flow<PagingData<NumberItem>> = paginatedFlow(
        strategy = OffsetLimitStrategy(PagingRefreshPolicy.RESET_TO_INITIAL),
        pagingConfig = PagingConfig(
            pageSize = 10,
            initialLoadSize = 10,
            prefetchDistance = 1,
            enablePlaceholders = false
        ),
        fetch = { offset, limit ->
            Timber.tag("PagingLog").d("OffsetLimit fetch -> offset: $offset, limit: $limit")
            val res = remoteDataSource.getItemsWithLimitOffset(offset = offset, limit = limit)
            Timber.tag("PagingLog").d("OffsetLimit fetched -> items count: ${res.data.size}")
            PagingResult(res.data)
        },
        mapper = NumberResponse::toDomain
    )

    override fun getLongListCursor(): Flow<PagingData<NumberItem>> = paginatedFlow(
        strategy = CursorStrategy(),
        pagingConfig = PagingConfig(
            pageSize = 10,
            initialLoadSize = 10,
            prefetchDistance = 1,
            enablePlaceholders = false
        ),
        fetch = { cursor, size ->
            Timber.tag("PagingLog").d("Cursor fetch -> cursor: ${cursor.value}, size: $size")
            val res = remoteDataSource.getItemsWithCursor(cursor.value, size)
            Timber.tag("PagingLog")
                .d("Cursor fetched -> items count: ${res.data.size}, nextCursor: ${res.nextCursor}, prevCursor: ${res.prevCursor}")
            PagingResult(
                items = res.data,
                nextCursor = res.nextCursor,
                prevCursor = res.prevCursor
            )
        },
        mapper = NumberResponse::toDomain
    )

    override fun getLongListPageSized(): Flow<PagingData<NumberItem>> = paginatedFlow(
        strategy = PageSizeStrategy(refreshPolicy = PagingRefreshPolicy.RESET_TO_INITIAL),
        pagingConfig = PagingConfig(
            pageSize = 10,
            initialLoadSize = 10,
            prefetchDistance = 1,
            enablePlaceholders = false
        ),
        fetch = { page, size ->
            Timber.tag("PagingLog").d("PageSized fetch -> page: $page, size: $size")
            val res = remoteDataSource.getItemsWithPageSize(page = page, size = size)
            Timber.tag("PagingLog").d("PageSized fetched -> items count: ${res.data.size}")
            PagingResult(res.data)
        },
        mapper = NumberResponse::toDomain
    )

    override fun getTransactionsByDateRange(
        from: LocalDate,
        to: LocalDate
    ) = paginatedFlow(
        strategy = DateWindowStrategy(from..to),
        pagingConfig = PagingConfig(
            pageSize = 10,
            initialLoadSize = 10,
            prefetchDistance = 1,
            enablePlaceholders = false
        ),
        fetch = { key, size ->
            Timber.tag("PagingLog").d("DateWindow fetch -> window: $key, size: $size")
            val res = remoteDataSource.getTransactionsByDateRange(
                from = key.dateWindow.start,
                to = key.dateWindow.endExclusive,
                limit = size,
                offset = key.offset
            )
            Timber.tag("PagingLog").d("PageSized fetched -> items count: ${res.data.size}")
            PagingResult(res.data)
        },
        mapper = TransactionResponse::toDomain
    )

}
