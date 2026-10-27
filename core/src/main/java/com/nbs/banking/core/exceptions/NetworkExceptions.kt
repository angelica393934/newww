package com.nbs.banking.core.exceptions

import com.nbs.banking.core.error.AppException
import com.nbs.banking.core.extensions.emptyString

open class NetworkException(
    cause: Throwable? = null,
    message: String? = null,
) : AppException(
    message = message ?: "Network operation failed",
    cause = cause,
)

class ApiException(
    cause: Throwable? = null,
    val httpStatusCode: Int,
    val code: String = emptyString(),
    message: String? = null,
) : NetworkException(
    message = message ?: "An API error occurred",
    cause = cause,
)

class HostResolutionException(
    cause: Throwable?,
    message: String? = null,
) : NetworkException(
    message = message ?: "Unable to resolve host",
    cause = cause
)

class TimeoutException(
    cause: Throwable?,
    message: String? = null,
) : NetworkException(
    message = message ?: "Network operation timed out",
    cause = cause
)