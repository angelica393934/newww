package com.nbs.banking.longlist.data.remote

import com.nbs.banking.longlist.data.model.CursorResponse
import com.nbs.banking.longlist.data.model.DateRangeResponse
import com.nbs.banking.longlist.data.model.LimitOffsetResponse
import com.nbs.banking.longlist.data.model.NumberResponse
import com.nbs.banking.longlist.data.model.PageSizeResponse
import com.nbs.banking.longlist.data.model.TransactionResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LongListRemoteDataSourceTest {

    private val apiClient: LongListFakeApiClient = mockk()
    private lateinit var remoteDataSource: LongListRemoteDataSourceImpl

    @Before
    fun setUp() {
        remoteDataSource = LongListRemoteDataSourceImpl(apiClient)
    }

    @Test
    fun `when getItemsWithPageSize called then delegates to apiClient`() = runTest {
        // Given
        val expected = PageSizeResponse(data = listOf(NumberResponse(1)), page = 1, size = 10)
        coEvery { apiClient.getItemsWithPageSize(1, 10) } returns expected

        // When
        val result = remoteDataSource.getItemsWithPageSize(1, 10)

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { apiClient.getItemsWithPageSize(1, 10) }
    }

    @Test
    fun `when getItemsWithLimitOffset called then delegates to apiClient`() = runTest {
        // Given
        val expected = LimitOffsetResponse(data = listOf(NumberResponse(2)), limit = 10, offset = 0)
        coEvery { apiClient.getItemsWithLimitOffset(10, 0) } returns expected

        // When
        val result = remoteDataSource.getItemsWithLimitOffset(10, 0)

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { apiClient.getItemsWithLimitOffset(10, 0) }
    }

    @Test
    fun `when getItemsWithCursor called then delegates to apiClient`() = runTest {
        // Given
        val expected = CursorResponse(
            data = listOf(NumberResponse(3)),
            nextCursor = "cursor_10",
            prevCursor = null
        )
        coEvery { apiClient.getItemsWithCursor("cursor_0", 10) } returns expected

        // When
        val result = remoteDataSource.getItemsWithCursor("cursor_0", 10)

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { apiClient.getItemsWithCursor("cursor_0", 10) }
    }

    @Test
    fun `when getTransactionsByDateRange called then delegates to apiClient`() = runTest {
        // Given
        val from = LocalDate(2024, 1, 1)
        val to = LocalDate(2024, 3, 1)
        val expected = DateRangeResponse(
            data = listOf(
                TransactionResponse(
                    id = 1,
                    date = from,
                    title = "Test Payment",
                    amountCents = 1000L
                )
            )
        )
        coEvery { apiClient.getTransactionsByDateRange(from, to, 10, 0) } returns expected

        // When
        val result = remoteDataSource.getTransactionsByDateRange(from, to, 10, 0)

        // Then
        assertEquals(expected, result)
        coVerify(exactly = 1) { apiClient.getTransactionsByDateRange(from, to, 10, 0) }
    }
}
