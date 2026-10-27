package com.nbs.banking.randomactivityidea.presentation

import app.cash.turbine.test
import com.nbs.banking.MainCoroutineRule
import com.nbs.banking.core.exceptions.ApiException
import com.nbs.banking.core.state.ScreenState
import com.nbs.banking.randomactivityidea.domain.model.RandomActivityIdea
import com.nbs.banking.randomactivityidea.domain.usecase.GetRandomActivityUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class RandomActivityIdeaViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val getRandomActivityUseCase: GetRandomActivityUseCase = mockk()
    private lateinit var viewModel: RandomActivityIdeaViewModel

    private fun createViewModel() {
        viewModel = RandomActivityIdeaViewModel(getRandomActivityUseCase)
    }

    private val mockActivity = RandomActivityIdea(
        activity = "Go for a walk",
        availability = 1.0,
        type = "recreational",
        participants = 1,
        price = 0.0,
        accessibility = "Easy",
        duration = "30 mins",
        kidFriendly = true,
        link = "",
        key = "654321",
    )

    // region initial state

    @Test
    fun `given viewModel created when observed then initial state is default`() = runTest {
        // given
        createViewModel()

        // when / then
        viewModel.state.test {
            assertEquals(ScreenState.default<RandomActivityIdea>(), awaitItem().activityState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion

    // region FetchRandomActivity intent

    @Test
    fun `when FetchRandomActivity intent succeeds then state is success`() = runTest {
        // given
        coEvery { getRandomActivityUseCase() } coAnswers { mockActivity }
        createViewModel()

        viewModel.state.test {
            assertEquals(ScreenState.default<RandomActivityIdea>(), awaitItem().activityState)

            // when
            viewModel.onIntent(RandomActivityIdeaIntent.FetchRandomActivity)

            // then
            assertEquals(ScreenState.success(mockActivity), awaitItem().activityState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when FetchRandomActivity intent succeeds then loading emitted before success`() = runTest {
        // given
        coEvery { getRandomActivityUseCase() } coAnswers {
            delay(100.milliseconds)
            mockActivity
        }
        createViewModel()

        viewModel.state.test {
            assertEquals(ScreenState.default<RandomActivityIdea>(), awaitItem().activityState)

            // when
            viewModel.onIntent(RandomActivityIdeaIntent.FetchRandomActivity)

            // then
            assertEquals(ScreenState.loading<RandomActivityIdea>(), awaitItem().activityState)
            assertEquals(ScreenState.success(mockActivity), awaitItem().activityState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when FetchRandomActivity intent fails then loading emitted before error`() = runTest {
        // given
        val error = ApiException(httpStatusCode = 500, message = "Server error")
        coEvery { getRandomActivityUseCase() } coAnswers {
            delay(100.milliseconds) // give loading state time to emit
            throw error
        }
        createViewModel()

        // when / then
        viewModel.state.test {
            assertEquals(ScreenState.default<RandomActivityIdea>(), awaitItem().activityState)

            viewModel.onIntent(RandomActivityIdeaIntent.FetchRandomActivity)

            assertEquals(ScreenState.loading<RandomActivityIdea>(), awaitItem().activityState)
            val errorState = awaitItem().activityState as ScreenState.Error
            assertEquals("Server error", errorState.message)
            assertEquals(error, errorState.throwable)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when FetchRandomActivity intent fails then snackbar effect emitted`() = runTest {
        val error = ApiException(httpStatusCode = 500, message = "Server error")
        coEvery { getRandomActivityUseCase() } coAnswers {
            delay(100.milliseconds)
            throw error
        }
        createViewModel()

        // when / then
        viewModel.effect.test {
            viewModel.onIntent(RandomActivityIdeaIntent.FetchRandomActivity)

            val effect = awaitItem() as RandomActivityIdeaEffect.ShowSnackBar
            assertEquals("Server error", effect.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion

    // region reduce

    @Test
    fun `reduce ActivityLoading returns loading state`() {
        // given
        createViewModel()
        val initialState = RandomActivityIdeaState()

        // when
        val result = viewModel.reduce(
            initialState,
            RandomActivityIdeaEvent.ActivityLoading
        )

        // then
        assertEquals(ScreenState.loading<RandomActivityIdea>(), result.activityState)
    }

    @Test
    fun `reduce ActivityLoaded returns success state`() {
        // given
        createViewModel()
        val initialState = RandomActivityIdeaState()

        // when
        val result = viewModel.reduce(
            initialState,
            RandomActivityIdeaEvent.ActivityLoaded(mockActivity)
        )

        // then
        assertEquals(ScreenState.success(mockActivity), result.activityState)
    }

    @Test
    fun `reduce ActivityError returns error state`() {
        // given
        createViewModel()
        val error = ApiException(httpStatusCode = 500, message = "Server error")
        val initialState = RandomActivityIdeaState()

        // when
        val result = viewModel.reduce(
            initialState,
            RandomActivityIdeaEvent.ActivityError(error)
        )

        // then
        assertEquals(
            ScreenState.error<RandomActivityIdea>(error, error.message.orEmpty()),
            result.activityState
        )
    }

    // endregion


}
