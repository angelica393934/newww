package com.nbs.banking.longlist.presentation.datarange

import androidx.paging.PagingData
import app.cash.turbine.test
import com.nbs.banking.MainCoroutineRule
import com.nbs.banking.longlist.domain.model.Transaction
import com.nbs.banking.longlist.domain.usecase.TransactionUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionListViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val transactionUseCase: TransactionUseCase = mockk()

    private lateinit var viewModel: TransactionListViewModel

    private val defaultFrom = LocalDate(2024, 1, 1)

    @Before
    fun setUp() {
        // Init load default stub
        every { transactionUseCase(any(), any()) } returns flowOf(
            PagingData.from(
                listOf(Transaction(id = 1, date = defaultFrom, title = "Payment #1", amountCents = 1000L))
            )
        )
    }

    private fun createViewModel() {
        viewModel = TransactionListViewModel(transactionUseCase)
    }

    @Test
    fun `when SetRange intent received then updates state range`() = runTest {
        // Given
        createViewModel()
        val newFrom = LocalDate(2024, 2, 1)
        val newTo = LocalDate(2024, 4, 1)

        // When
        viewModel.onIntent(TransactionIntent.SetRange(newFrom, newTo))

        // Then
        viewModel.state.test {
            val updatedState = awaitItem()
            assertEquals(newFrom, updatedState.from)
            assertEquals(newTo, updatedState.to)
        }
    }

    @Test
    fun `when ViewModel initialized then items emits transformed paging data`() = runTest {
        // Given / When
        createViewModel()

        // Then
        viewModel.items.test {
            val pagingData = awaitItem()
            assertNotNull(pagingData)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
