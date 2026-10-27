package com.nbs.banking.core.pagination.strategy

import com.nbs.banking.core.pagination.model.PagingResult
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

data class DateWindow(val start: LocalDate, val endExclusive: LocalDate)

data class DateWindowKey(val dateWindow: DateWindow, val offset: Int = 0)


// Date window is the KEY, one calendar month, paged newest to oldest.
// Windows are half-open: [start, endExclusive)
//
// Example 1: range = 1 Jan..31 Mar
// key = null (initialKey) -> [1 Mar, 1 Apr),   prevKey = null, nextKey = [1 Feb, 1 Mar)
// key = [1 Feb, 1 Mar),      prevKey = null,   nextKey = [1 Jan, 1 Feb)
// key = [1 Jan, 1 Feb),      prevKey = null,   nextKey = null (start <= range.start, last window)
//
// Example 2: range = 15 Jan..20 Mar (partial first and last months)
// key = null (initialKey) -> [1 Mar, 21 Mar),  nextKey = [1 Feb, 1 Mar)   (clamped to range end)
// key = [1 Feb, 1 Mar),      nextKey = [15 Jan, 1 Feb)                    (clamped to range start)
// key = [15 Jan, 1 Feb),     nextKey = null
class DateWindowStrategy(
    private val range: ClosedRange<LocalDate>,
) : PaginationStrategy<DateWindowKey> {

    // range.endInclusive = 20 Mar -> exclusive end = 21 Mar
    private val rangeEndExclusive = range.endInclusive.plus(1, DateTimeUnit.DAY)

    // Window ending at a 1st of a month: goes back exactly one month, clamped to range.start
    // windowEndingAt(1 Mar) -> [1 Feb, 1 Mar)
    private fun windowEndingAt(endExclusive: LocalDate) = DateWindow(
        start = maxOf(endExclusive.minus(1, DateTimeUnit.MONTH), range.start),
        endExclusive = endExclusive,
    )

    // First load: the month containing the range end, offset 0.
    // Starts on the 1st of that month and stops at the range end.
    override val initialKey = DateWindowKey(
        dateWindow = DateWindow(
            start = maxOf(LocalDate(range.endInclusive.year, range.endInclusive.month, 1), range.start),
            endExclusive = rangeEndExclusive,
        ),
        offset = 0,
    )

    // Forward only (newest to oldest), nothing newer to prepend
    override fun prevKey(key: DateWindowKey, loadSize: Int, result: PagingResult<*>): DateWindowKey? = null


    override fun nextKey(key: DateWindowKey, loadSize: Int, result: PagingResult<*>): DateWindowKey? =
        when {
            // full page: this month may have more, continue at the next offset
            result.items.size >= loadSize -> key.copy(offset = key.offset + result.items.size)
            // short page and the range start is reached: nothing older
            key.dateWindow.start <= range.start -> null
            // short page: this month is done, go to the previous month from offset 0
            else -> DateWindowKey(windowEndingAt(key.dateWindow.start), 0)
        }

    // Refresh restarts from the newest window (initialKey)
    override fun refreshKey(prevKey: DateWindowKey?, nextKey: DateWindowKey?, pageSize: Int): DateWindowKey? = null
}