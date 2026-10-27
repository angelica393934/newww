package com.nbs.banking.longlist.domain.usecase

import androidx.paging.PagingData
import app.cash.turbine.test
import com.nbs.banking.longlist.domain.LongListRepository
import com.nbs.banking.longlist.domain.model.NumberItem
import com.nbs.banking.longlist.domain.model.Transaction
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LongListUseCasesTest {

    private val repository: LongListRepository = mockk()

    private lateinit var longListUseCase: LongListUseCaseImpl
    private lateinit var transactionUseCase: TransactionUseCaseImpl

    @Before
    fun setUp() {
        longListUseCase = LongListUseCaseImpl(repository)
        transactionUseCase = TransactionUseCaseImpl(repository)
    }

    @Test
    fun `when LongListUseCase invoked then delegates to repository getLongListOffsetLimit`() = runTest {
        // Given
        val expectedData = PagingData.from(listOf(NumberItem(1)))
        every { repository.getLongListOffsetLimit() } returns flowOf(expectedData)

        // When
        val flow = longListUseCase()

        // Then
        flow.test {
            val item = awaitItem()
            assertEquals(expectedData, item)
            awaitComplete()
        }
    }

    @Test
    fun `when TransactionUseCase invoked then delegates to repository getTransactionsByDateRange`() = runTest {
        // Given
        val from = LocalDate(2024, 1, 1)
        val to = LocalDate(2024, 3, 1)
        val expectedData = PagingData.from(
            listOf(Transaction(id = 1, date = from, title = "T1", amountCents = 100))
        )
        every { repository.getTransactionsByDateRange(from, to) } returns flowOf(expectedData)

        // When
        val flow = transactionUseCase(from, to)

        // Then
        flow.test {
            val item = awaitItem()
            assertEquals(expectedData, item)
            awaitComplete()
        }
    }
}
