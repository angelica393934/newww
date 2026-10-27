package com.nbs.banking.corenetwork.utils

import com.nbs.banking.core.exceptions.ApiException
import com.nbs.banking.core.exceptions.HostResolutionException
import com.nbs.banking.core.exceptions.NetworkException
import com.nbs.banking.core.exceptions.TimeoutException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> safeApiCall(block: suspend () -> Response<T>): T {
    return try {
        val response = block()
        if (!response.isSuccessful) {
            throw ApiException(
                httpStatusCode = response.code(),
                message = response.message(),
            )
        }
        response.body() ?: throw ApiException(
            httpStatusCode = response.code(),
            code = "E204",
            message = "Response body is null"
        )
    } catch (e: CancellationException) {
        throw e
    } catch (e: UnknownHostException) {
        throw HostResolutionException(e, e.message.orEmpty())
    } catch (e: SocketTimeoutException) {
        throw TimeoutException(e, e.message.orEmpty())
    } catch (e: IOException) {
        throw NetworkException(e, e.message.orEmpty())
    }
}

fun <T> Flow<T>.catchNetworkException(): Flow<T> =
    catch { e ->
        when (e) {
            is CancellationException -> throw e
            is UnknownHostException -> throw HostResolutionException(e, e.message.orEmpty())
            is SocketTimeoutException -> throw TimeoutException(e, e.message.orEmpty())
            is IOException -> throw NetworkException(e, e.message.orEmpty())
            else -> throw e
        }
    }