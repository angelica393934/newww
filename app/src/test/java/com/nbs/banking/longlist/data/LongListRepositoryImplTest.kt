package com.nbs.banking.longlist.data

import app.cash.turbine.test
import com.nbs.banking.longlist.data.model.CursorResponse
import com.nbs.banking.longlist.data.model.DateRangeResponse
import com.nbs.banking.longlist.data.model.LimitOffsetResponse
import com.nbs.banking.longlist.data.model.NumberResponse
import com.nbs.banking.longlist.data.model.PageSizeResponse
import com.nbs.banking.longlist.data.model.TransactionResponse
import com.nbs.banking.longlist.data.remote.LongListRemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class LongListRepositoryImplTest {

    private val remoteDataSource: LongListRemoteDataSource = mockk()
    private lateinit var repository: LongListRepositoryImpl

    @Before
    fun setUp() {
        repository = LongListRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `when getLongListOffsetLimit called then returns paging flow`() = runTest {
        // Given
        coEvery {
            remoteDataSource.getItemsWithLimitOffset(offset = 0, limit = 10)
        } returns LimitOffsetResponse(data = listOf(NumberResponse(1)), limit = 10, offset = 0)

        // When
        val flow = repository.getLongListOffsetLimit()

        // Then
        flow.test {
            val pagingData = awaitItem()
            assertNotNull(pagingData)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when getLongListCursor called then returns paging flow`() = runTest {
        // Given
        coEvery {
            remoteDataSource.getItemsWithCursor(cursor = null, limit = 10)
        } returns CursorResponse(data = listOf(NumberResponse(1)), nextCursor = null, prevCursor = null)

        // When
        val flow = repository.getLongListCursor()

        // Then
        flow.test {
            val pagingData = awaitItem()
            assertNotNull(pagingData)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when getLongListPageSized called then returns paging flow`() = runTest {
        // Given
        coEvery {
            remoteDataSource.getItemsWithPageSize(page = 1, size = 10)
        } returns PageSizeResponse(data = listOf(NumberResponse(1)), page = 1, size = 10)

        // When
        val flow = repository.getLongListPageSized()

        // Then
        flow.test {
            val pagingData = awaitItem()
            assertNotNull(pagingData)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when getTransactionsByDateRange called then returns paging flow`() = runTest {
        // Given
        val from = LocalDate(2024, 1, 1)
        val to = LocalDate(2024, 3, 1)
        coEvery {
            remoteDataSource.getTransactionsByDateRange(
                from = any(),
                to = any(),
                limit = any(),
                offset = any()
            )
        } returns DateRangeResponse(
            data = listOf(TransactionResponse(id = 1, date = from, title = "T1", amountCents = 100))
        )

        // When
        val flow = repository.getTransactionsByDateRange(from, to)

        // Then
        flow.test {
            val pagingData = awaitItem()
            assertNotNull(pagingData)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
