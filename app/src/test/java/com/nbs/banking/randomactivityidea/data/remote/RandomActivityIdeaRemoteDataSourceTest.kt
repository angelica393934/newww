package com.nbs.banking.randomactivityidea.data.remote

import com.nbs.banking.core.exceptions.ApiException
import com.nbs.banking.core.exceptions.HostResolutionException
import com.nbs.banking.core.exceptions.NetworkException
import com.nbs.banking.core.exceptions.TimeoutException
import com.nbs.banking.corenetwork.utils.safeApiCall
import com.nbs.banking.randomactivityidea.data.model.respone.RandomActivityIdeaResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

class RandomActivityIdeaRemoteDataSourceTest {

    private val apiClient: RandomActivityIdeaApiClient = mockk()
    private lateinit var remoteDataSource: RandomActivityIdeaRemoteDataSourceImpl

    private val mockResponse = RandomActivityIdeaResponse(
        activity = "Learn Kotlin",
        availability = 0.5,
        type = "education",
        participants = 1,
        price = 0.0,
        accessibility = "Medium",
        duration = "2 hours",
        kidFriendly = true,
        link = "https://kotlinlang.org",
        key = "112233",
    )

    @Before
    fun setUp() {
        remoteDataSource = RandomActivityIdeaRemoteDataSourceImpl(apiClient)
    }

    @Test
    fun `given apiClient returns success when getRandomActivity called then delegates and returns result`() = runTest {
        // given
        coEvery { apiClient.getRandomActivity() } returns Response.success(mockResponse)

        // when
        val result = remoteDataSource.getRandomActivity()

        // then
        assertEquals(mockResponse, result)
        coVerify(exactly = 1) { apiClient.getRandomActivity() }
    }

    @Test
    fun `given success response when safeApiCall then returns body`() = runTest {
        // given
        coEvery { apiClient.getRandomActivity() } returns Response.success(mockResponse)

        // when
        val result = safeApiCall { apiClient.getRandomActivity() }

        // then
        assertEquals(mockResponse, result)
    }

    @Test
    fun `given 404 response when safeApiCall then throws ApiException with correct code`() = runTest {
        // given
        val errorBody = "{\"error\":\"Not Found\"}".toResponseBody()
        coEvery { apiClient.getRandomActivity() } returns Response.error(404, errorBody)

        // when
        try {
            safeApiCall { apiClient.getRandomActivity() }
            fail("Expected ApiException")
        } catch (e: ApiException) {
            // then
            assertEquals(404, e.httpStatusCode)
        }
    }

    @Test
    fun `given 500 response when safeApiCall then throws ApiException with server error code`() = runTest {
        // given
        val errorBody = "{\"error\":\"Server Error\"}".toResponseBody()
        coEvery { apiClient.getRandomActivity() } returns Response.error(500, errorBody)

        // when
        try {
            safeApiCall { apiClient.getRandomActivity() }
            fail("Expected ApiException")
        } catch (e: ApiException) {
            // then
            assertEquals(500, e.httpStatusCode)
        }
    }

    @Test
    fun `given null body when safeApiCall then throws ApiException with respond body is null`() = runTest {
        // given
        coEvery { apiClient.getRandomActivity() } returns Response.success(null)

        // when
        try {
            safeApiCall { apiClient.getRandomActivity() }
            fail("Expected ApiException for null body")
        } catch (e: ApiException) {
            // then
            assertEquals(200, e.httpStatusCode)
            assertEquals("E204", e.code)
            assertEquals("Response body is null", e.message)
        }
    }

    @Test
    fun `given UnknownHostException when safeApiCall then throws HostResolutionException`() = runTest {
        // given
        val originalException = UnknownHostException("Host not found")
        coEvery { apiClient.getRandomActivity() } throws originalException

        // when
        try {
            safeApiCall { apiClient.getRandomActivity() }
            fail("Expected HostResolutionException")
        } catch (e: HostResolutionException) {
            // then
            assertEquals(originalException, e.cause)
            assertEquals("Host not found", e.message)
        }
    }

    @Test
    fun `given SocketTimeoutException when safeApiCall then throws TimeoutException`() = runTest {
        // given
        val originalException = SocketTimeoutException("Connection timed out")
        coEvery { apiClient.getRandomActivity() } throws originalException

        // when
        try {
            safeApiCall { apiClient.getRandomActivity() }
            fail("Expected TimeoutException")
        } catch (e: TimeoutException) {
            // then
            assertEquals(originalException, e.cause)
            assertEquals("Connection timed out", e.message)
        }
    }

    @Test
    fun `given IOException when safeApiCall then throws NetworkException`() = runTest {
        // given
        val originalException = IOException("Network failure")
        coEvery { apiClient.getRandomActivity() } throws originalException

        // when
        try {
            safeApiCall { apiClient.getRandomActivity() }
            fail("Expected NetworkException")
        } catch (e: NetworkException) {
            // then
            assertEquals(originalException, e.cause)
            assertEquals("Network failure", e.message)
        }
    }

    @Test
    fun `given CancellationException when safeApiCall then rethrows without wrapping`() = runTest {
        // given
        val originalException = CancellationException("Cancelled")
        coEvery { apiClient.getRandomActivity() } throws originalException

        // when
        try {
            safeApiCall { apiClient.getRandomActivity() }
            fail("Expected CancellationException")
        } catch (e: CancellationException) {
            // then
            assertSame(originalException, e)
        }
    }
}
