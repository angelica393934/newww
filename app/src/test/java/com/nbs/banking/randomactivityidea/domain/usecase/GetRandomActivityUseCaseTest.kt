package com.nbs.banking.randomactivityidea.domain.usecase

import com.nbs.banking.randomactivityidea.domain.RandomActivityIdeaRepository
import com.nbs.banking.randomactivityidea.domain.model.RandomActivityIdea
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetRandomActivityUseCaseTest {

    private val repository: RandomActivityIdeaRepository = mockk()
    private lateinit var useCase: GetRandomActivityUseCaseImpl

    private val expectedActivity = RandomActivityIdea(
        activity = "Listen to music",
        availability = 1.0,
        type = "music",
        participants = 1,
        price = 0.0,
        accessibility = "Easy",
        duration = "15 mins",
        kidFriendly = true,
        link = "",
        key = "778899",
    )

    @Before
    fun setUp() {
        useCase = GetRandomActivityUseCaseImpl(repository)
    }

    @Test
    fun `when GetRandomActivityUseCase invoked then delegates to repository`() = runTest {
        coEvery { repository.getRandomActivity() } returns expectedActivity

        val result = useCase()

        assertEquals(expectedActivity, result)
        coVerify { repository.getRandomActivity() }
    }
}
