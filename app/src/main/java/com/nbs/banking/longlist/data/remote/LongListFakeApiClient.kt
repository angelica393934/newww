package com.nbs.banking.longlist.data.remote

import com.nbs.banking.longlist.data.model.CursorResponse
import com.nbs.banking.longlist.data.model.DateRangeResponse
import com.nbs.banking.longlist.data.model.LimitOffsetResponse
import com.nbs.banking.longlist.data.model.NumberResponse
import com.nbs.banking.longlist.data.model.PageSizeResponse
import com.nbs.banking.longlist.data.model.TransactionResponse
import kotlinx.coroutines.delay
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import timber.log.Timber
import java.io.IOException
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class LongListFakeApiClient(
    initialFail: Boolean = false,
    private var loadMoreFail: Boolean = false,
) {
    private var failInitial = initialFail
    private var failLoadMore = loadMoreFail
    private var hasLoaded = false
    private suspend fun simulate(isInitial: Boolean) {
        delay(500.milliseconds)

        if (isInitial && hasLoaded) {
            failLoadMore = loadMoreFail
            hasLoaded = false
        }

        val fail = if (isInitial) {
            failInitial
        } else {
            failLoadMore
        }
        Timber.tag("PagingLog").d(
            "isInitial=$isInitial, fail=$fail, failInitial=$failInitial, failLoadMore=$failLoadMore"
        )
        if (fail) {
            if (isInitial) failInitial = false else failLoadMore = false
            throw IOException("Simulated network error")
        }

        if (isInitial) hasLoaded = true
    }

    private val items = generateSequence(NumberResponse(1)) {
        NumberResponse(it.number + 1)
    }.take(97).toList()

    suspend fun getItemsWithPageSize(page: Int, size: Int): PageSizeResponse<NumberResponse> {
        simulate(isInitial = page == 1)
        delay(500.milliseconds)
        val from = (page - 1) * size
        return PageSizeResponse(
            data = items.drop(from).take(size),
            page = page,
            size = size,
        )
    }

    suspend fun getItemsWithLimitOffset(
        limit: Int,
        offset: Int
    ): LimitOffsetResponse<NumberResponse> {
        simulate(isInitial = offset == 0)
        delay(500.milliseconds)
        val data = items.drop(offset).take(limit)
        return LimitOffsetResponse(
            data = data,
            limit = limit,
            offset = offset
        )
    }

    suspend fun getItemsWithCursor(cursor: String?, limit: Int): CursorResponse<NumberResponse> {
        simulate(isInitial = cursor == null)
        delay(500.milliseconds)
        val startIndex = cursor?.removePrefix("cursor_")?.toIntOrNull() ?: 0
        val data = items.drop(startIndex).take(limit)
        val nextIndex = startIndex + limit
        val nextCursor =
            if (nextIndex < items.size && data.isNotEmpty()) "cursor_$nextIndex" else null
        val prevCursor = if (startIndex > 0) "cursor_${maxOf(0, startIndex - limit)}" else null
        return CursorResponse(
            data = data,
            nextCursor = nextCursor,
            prevCursor = prevCursor
        )
    }

    private val transactions: List<TransactionResponse> by lazy {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        var nextId = 1
        (0 until 200).flatMap { offset ->
            val date = today.minus(offset, DateTimeUnit.DAY)
            (1..(offset % 4)).map { n ->
                val id = nextId++
                TransactionResponse(
                    id = id,
                    date = date,
                    title = "Payment #$id",
                    amountCents = 1_000L * (id % 50 + 1) + n,
                )
            }
        }
    }

    suspend fun getTransactionsByDateRange(
        from: LocalDate,
        to: LocalDate,
        limit: Int,
        offset: Int
    ): DateRangeResponse<TransactionResponse> {
        delay(500.milliseconds)
        return DateRangeResponse(
            data = transactions.filter { it.date in from..<to }
                .drop(offset)
                .take(limit)
        )
    }
}
