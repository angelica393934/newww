package com.nbs.banking.home.presentation

import app.cash.turbine.test
import com.nbs.banking.MainCoroutineRule
import com.nbs.banking.core.state.ScreenState
import com.nbs.banking.home.domain.dummyhome.model.HomeContent
import com.nbs.banking.home.domain.dummyhome.usecase.CheckHomeStatusUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.DismissBannerUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.FetchHomeContentUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.ObserveUnreadCountUseCase
import com.nbs.banking.home.presentation.home.mvvm.HomeViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class HomeContentViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val fetchHomeContentUseCase: FetchHomeContentUseCase = mockk()
    private val checkHomeStatusUseCase: CheckHomeStatusUseCase = mockk()
    private val observeUnreadCountUseCase: ObserveUnreadCountUseCase = mockk()
    private val dismissBannerUseCase: DismissBannerUseCase = mockk()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        every { fetchHomeContentUseCase() } returns flowOf()
        every { observeUnreadCountUseCase() } returns flowOf(0)
    }
    private fun createViewModel() {
        // init
        viewModel = HomeViewModel(
            fetchHomeContentUseCase,
            checkHomeStatusUseCase,
            observeUnreadCountUseCase,
            dismissBannerUseCase
        )
    }

    private val mockData = HomeContent("Title", "Description", "Author")

    // ─── Case 1: loadData() → homeContentState ────────────────────────────────

    @Test
    fun `when loadData called and fetch succeeds then homeContentState emits loading and success`() = runTest {
        // Given
        every { fetchHomeContentUseCase() } returns flow {
            delay(100.milliseconds)
            emit(mockData)
        }

        // When
        createViewModel()

        // Then
        viewModel.homeContentState.test {
            assertEquals(ScreenState.loading<HomeContent>(), awaitItem())
            val success = awaitItem() as ScreenState.Success
            assertEquals(mockData, success.data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when loadData called and fetch fails then homeContentState emits loading and error`() = runTest {
        // Given
        val exception = RuntimeException("Network Error")
        every { fetchHomeContentUseCase() } returns flow {
            delay(100.milliseconds)
            throw exception
        }

        // When
        createViewModel()

        // Then
        viewModel.homeContentState.test {
            assertEquals(ScreenState.loading<HomeContent>(), awaitItem())
            val error = awaitItem() as ScreenState.Error
            assertEquals("Network Error", error.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ─── Case 2: checkStatus() → statusState ─────────────────────────────────
    @Test
    fun `when checkStatus called and succeeds then statusState emits loading and success`() = runTest {
        // Given
        coEvery { checkHomeStatusUseCase() } coAnswers {
            delay(100.milliseconds)
            true
        }
        createViewModel()

        // When
        viewModel.statusState.test {
            assertEquals(ScreenState.default<Boolean>(), awaitItem())
            viewModel.checkStatus()
            assertEquals(ScreenState.loading<Boolean>(), awaitItem())
            val success = awaitItem() as ScreenState.Success
            assertEquals(true, success.data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when checkStatus called and fails then statusState emits loading and error`() = runTest {
        // Given
        val exception = RuntimeException("Status Error")
        coEvery { checkHomeStatusUseCase() } coAnswers {
            delay(100.milliseconds)
            throw exception
        }
        createViewModel()

        // When
        viewModel.statusState.test {
            assertEquals(ScreenState.default<Boolean>(), awaitItem())
            viewModel.checkStatus()
            assertEquals(ScreenState.loading<Boolean>(), awaitItem())
            val error = awaitItem() as ScreenState.Error
            assertEquals("Status Error", error.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ─── Case 3: loadData() → unreadCount ────────────────────────────────────

    @Test
    fun `when loadData called then unreadCount updates`() = runTest {
        // Given
        every { observeUnreadCountUseCase() } returns flowOf(5)

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(5, viewModel.unreadCount)
    }

    @Test
    fun `when observeUnreadCount fails then unreadCount stays zero`() = runTest {
        // Given
        coEvery { observeUnreadCountUseCase() } returns flow {
            throw RuntimeException("Badge sync failed")
        }

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(0, viewModel.unreadCount)
    }

    // ─── Case 4: dismissBanner() → bannerActionResult ─────────────────────────
    @Test
    fun `when dismissBanner called and succeeds then bannerActionResult updates`() = runTest {
        // Given
        coEvery { dismissBannerUseCase(any()) } coAnswers {
            delay(100.milliseconds)
            true
        }
        createViewModel()

        // When
        viewModel.dismissBanner("banner_1")
        advanceUntilIdle()

        // Then
        assertEquals("Banner banner_1 dismissed!", viewModel.bannerActionResult)
    }

    @Test
    fun `when dismissBanner called and fails then bannerActionResult stays null`() = runTest {
        // Given
        coEvery { dismissBannerUseCase(any()) } throws RuntimeException("Dismiss failed")
        createViewModel()

        // When
        viewModel.dismissBanner("banner_1")
        advanceUntilIdle()

        // Then
        assertNull(viewModel.bannerActionResult)
    }

}
