package com.nbs.banking.randomactivityidea.data

import com.nbs.banking.randomactivityidea.data.model.respone.RandomActivityIdeaResponse
import com.nbs.banking.randomactivityidea.data.remote.RandomActivityIdeaRemoteDataSource
import com.nbs.banking.randomactivityidea.domain.model.RandomActivityIdea
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RandomActivityIdeaRepositoryImplTest {

    private val remoteDataSource: RandomActivityIdeaRemoteDataSource = mockk()
    private lateinit var repository: RandomActivityIdeaRepositoryImpl

    private val response = RandomActivityIdeaResponse(
        activity = "Play chess",
        availability = 1.0,
        type = "procedural",
        participants = 2,
        price = 0.0,
        accessibility = "Easy",
        duration = "30 mins",
        kidFriendly = true,
        link = "",
        key = "445566",
    )

    private val expectedDomain = RandomActivityIdea(
        activity = "Play chess",
        availability = 1.0,
        type = "procedural",
        participants = 2,
        price = 0.0,
        accessibility = "Easy",
        duration = "30 mins",
        kidFriendly = true,
        link = "",
        key = "445566",
    )

    @Before
    fun setUp() {
        repository = RandomActivityIdeaRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `when getRandomActivity called then fetches from remote and maps to domain`() = runTest {
        coEvery { remoteDataSource.getRandomActivity() } returns response

        val result = repository.getRandomActivity()

        assertEquals(expectedDomain, result)
    }
}
