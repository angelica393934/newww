package com.nbs.banking.longlist.presentation.longlist

import androidx.paging.PagingData
import app.cash.turbine.test
import com.nbs.banking.MainCoroutineRule
import com.nbs.banking.longlist.domain.model.NumberItem
import com.nbs.banking.longlist.domain.usecase.LongListUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LongListViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val longListUseCase: LongListUseCase = mockk()

    private lateinit var viewModel: LongListViewModel

    @Before
    fun setUp() {
        // Init load default stub
        every { longListUseCase() } returns flowOf(PagingData.from(listOf(NumberItem(1))))
    }

    private fun createViewModel() {
        viewModel = LongListViewModel(longListUseCase)
    }

    @Test
    fun `when ViewModel initialized then items emits paging data`() = runTest {
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
