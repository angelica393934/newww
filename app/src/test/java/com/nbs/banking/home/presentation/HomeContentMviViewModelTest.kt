package com.nbs.banking.home.presentation

import app.cash.turbine.test
import com.nbs.banking.MainCoroutineRule
import com.nbs.banking.core.state.ScreenState
import com.nbs.banking.core.state.isLoading
import com.nbs.banking.home.domain.dummyhome.model.HomeContent
import com.nbs.banking.home.domain.dummyhome.usecase.CheckHomeStatusUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.DismissBannerUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.FetchHomeContentUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.ObserveUnreadCountUseCase
import com.nbs.banking.home.presentation.home.mvi.HomeEffect
import com.nbs.banking.home.presentation.home.mvi.HomeEvent
import com.nbs.banking.home.presentation.home.mvi.HomeIntent
import com.nbs.banking.home.presentation.home.mvi.HomeMviViewModel
import com.nbs.banking.home.presentation.home.mvi.HomeState
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
class HomeContentMviViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val fetchHomeContentUseCase: FetchHomeContentUseCase = mockk()
    private val checkHomeStatusUseCase: CheckHomeStatusUseCase = mockk()
    private val observeUnreadCountUseCase: ObserveUnreadCountUseCase = mockk()
    private val dismissBannerUseCase: DismissBannerUseCase = mockk()

    private lateinit var viewModel: HomeMviViewModel

    @Before
    fun setUp() {
        // init block from viewmodel
        every { fetchHomeContentUseCase() } returns flowOf()
        every { observeUnreadCountUseCase() } returns flowOf(0)
    }

    private fun createViewModel() {
        viewModel = HomeMviViewModel(
            fetchHomeContentUseCase,
            checkHomeStatusUseCase,
            observeUnreadCountUseCase,
            dismissBannerUseCase
        )
    }

    private val mockData = HomeContent("Title", "Description", "Author")

    // ─── LoadHomeContent ──────────────────────────────────────────────────────
    @Test
    fun `when LoadHomeContent then state emits loading and success`() = runTest {
        // Given
        every { fetchHomeContentUseCase() } returns flow {
            delay(100.milliseconds)
            emit(mockData)
        }

        // When
        createViewModel()

        viewModel.state.test {
            val loadingState = awaitItem()
            assertEquals(true, loadingState.homeContent.isLoading())

            // Then — state
            val successState = awaitItem()
            val success = successState.homeContent as ScreenState.Success
            assertEquals(mockData, success.data)

            cancelAndIgnoreRemainingEvents()
        }

        viewModel.effect.test {
            // then — effect
            val effect = awaitItem() as HomeEffect.ShowSnackBar
            assertEquals("Home content loaded successfully", effect.message)
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `when LoadHomeContent fails then state emits loading and error`() = runTest {
        // Given
        val exception = RuntimeException("Network Error")
        every { fetchHomeContentUseCase() } returns flow {
            delay(100.milliseconds)
            throw exception
        }

        // When
        createViewModel()

        viewModel.state.test {
            val loadingState = awaitItem()
            assertEquals(true, loadingState.homeContent.isLoading())

            // Then — state
            val errorState = awaitItem()
            val error = errorState.homeContent as ScreenState.Error
            assertEquals("Network Error", error.message)

            cancelAndIgnoreRemainingEvents()
        }

        viewModel.effect.test {
            // then — effect
            val effect = awaitItem() as HomeEffect.ShowSnackBar
            assertEquals("Network Error", effect.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ─── UnreadCount ──────────────────────────────────────────────────────────
    @Test
    fun `when unreadCount emits then state updates unreadCount`() = runTest {
        // Given
        every { observeUnreadCountUseCase() } returns flowOf(5)

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(5, viewModel.state.value.unreadCount)
    }

    @Test
    fun `when observeUnreadCount fails then unreadCount stays zero`() = runTest {
        // Given
        every { observeUnreadCountUseCase() } returns flow {
            throw RuntimeException("Badge sync failed")
        }

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(0, viewModel.state.value.unreadCount)
    }

    // ─── CheckStatus ──────────────────────────────────────────────────────────
    @Test
    fun `when CheckStatus succeeds then state emits loading and success`() = runTest {
        // Given
        coEvery { checkHomeStatusUseCase() } coAnswers {
            delay(100.milliseconds)
            true
        }
        createViewModel()
        viewModel.state.test {
            skipItems(1) // skip state from LoadHomeContent init

            // When
            viewModel.onIntent(HomeIntent.CheckStatus)

            // Then — loading
            val loadingState = awaitItem()
            assertEquals(true, loadingState.status.isLoading())

            // Then — success
            val successState = awaitItem()
            val success = successState.status as ScreenState.Success
            assertEquals(true, success.data)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when CheckStatus fails then state emits loading and error`() = runTest {
        // Given
        val exception = RuntimeException("Status Error")
        coEvery { checkHomeStatusUseCase() } coAnswers {
            delay(100.milliseconds)
            throw exception
        }

        createViewModel()
        viewModel.state.test {
            skipItems(1) // skip state from LoadHomeContent init

            // When
            viewModel.onIntent(HomeIntent.CheckStatus)

            // Then — loading
            val loadingState = awaitItem()
            assertEquals(true, loadingState.status.isLoading())

            // Then — error
            val errorState = awaitItem()
            val error = errorState.status as ScreenState.Error
            assertEquals("Status Error", error.message)

            cancelAndIgnoreRemainingEvents()
        }

        viewModel.effect.test {
            // then — effect
            val effect = awaitItem() as HomeEffect.ShowSnackBar
            assertEquals("Status Error", effect.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ─── DismissBanner ────────────────────────────────────────────────────────

    @Test
    fun `when DismissBanner succeeds then state updates bannerActionResult and emits effect`() =
        runTest {
            // Given
            coEvery { dismissBannerUseCase(any()) } coAnswers {
                delay(100.milliseconds)
                true
            }
            createViewModel()

            // When
            viewModel.state.test {
                skipItems(1)
                viewModel.onIntent(HomeIntent.DismissBanner("banner_1"))

                // Then — state
                val state = awaitItem()
                assertEquals("Banner banner_1 dismissed!", state.bannerActionResult)
            }

            viewModel.effect.test {
                // then — effect
                val effect = awaitItem() as HomeEffect.ShowSnackBar
                assertEquals("Banner banner_1 dismissed!", effect.message)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `when DismissBanner fails then bannerActionResult stays null and emits error effect`() =
        runTest {
            // Given
            coEvery { dismissBannerUseCase(any()) } coAnswers {
                delay(100.milliseconds)
                throw RuntimeException("Dismiss failed")
            }
            createViewModel()

            // When
            viewModel.onIntent(HomeIntent.DismissBanner("banner_1"))

            // Then — state
            assertNull(viewModel.state.value.bannerActionResult)

            viewModel.effect.test {
                // then — effect
                val effect = awaitItem() as HomeEffect.ShowSnackBar
                assertEquals("Failed to dismiss: Dismiss failed", effect.message)
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ─── Reducer ──────────────────────────────────────────────────────────────

    @Test
    fun `reduce HomeContentLoading returns contentLoading state`() {
        // Given
        createViewModel()

        // When
        val result = viewModel.reduce(HomeState(), HomeEvent.HomeContentLoading)

        // Then
        assertEquals(true, result.homeContent.isLoading())
    }

    @Test
    fun `reduce HomeContentLoaded returns contentSuccess state`() {
        // Given
        createViewModel()

        // When
        val result = viewModel.reduce(HomeState(), HomeEvent.HomeContentLoaded(mockData))

        // Then
        val success = result.homeContent as ScreenState.Success
        assertEquals(mockData, success.data)
    }

    @Test
    fun `reduce HomeContentError returns contentError state`() {
        // Given
        val error = RuntimeException("Network Error")
        createViewModel()

        // When
        val result = viewModel.reduce(HomeState(), HomeEvent.HomeContentError(error))

        // Then
        val errorState = result.homeContent as ScreenState.Error
        assertEquals("Network Error", errorState.message)
    }

    @Test
    fun `reduce StatusLoading returns statusLoading state`() {
        // Given
        createViewModel()

        // When
        val result = viewModel.reduce(HomeState(), HomeEvent.StatusLoading)

        // Then
        assertEquals(true, result.status.isLoading())
    }

    @Test
    fun `reduce StatusLoaded returns statusSuccess state`() {
        // Given
        createViewModel()

        // When
        val result = viewModel.reduce(HomeState(), HomeEvent.StatusLoaded(true))

        // Then
        val success = result.status as ScreenState.Success
        assertEquals(true, success.data)
    }

    @Test
    fun `reduce StatusError returns statusError state`() {
        // Given
        val error = RuntimeException("Status Error")
        createViewModel()

        // When
        val result = viewModel.reduce(HomeState(), HomeEvent.StatusError(error))

        // Then
        val errorState = result.status as ScreenState.Error
        assertEquals("Status Error", errorState.message)
    }

    @Test
    fun `reduce UnreadCountUpdated returns updated unreadCount`() {
        // Given
        createViewModel()

        // When
        val result = viewModel.reduce(HomeState(), HomeEvent.UnreadCountUpdated(10))

        // Then
        assertEquals(10, result.unreadCount)
    }

    @Test
    fun `reduce BannerDismissed returns updated bannerActionResult`() {
        // Given
        createViewModel()

        // When
        val result = viewModel.reduce(HomeState(), HomeEvent.BannerDismissed("Banner dismissed!"))

        // Then
        assertEquals("Banner dismissed!", result.bannerActionResult)
    }

}
