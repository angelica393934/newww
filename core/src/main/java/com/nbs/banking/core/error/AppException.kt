package com.nbs.banking.core.error

open class AppException(
    cause: Throwable? = null,
    message: String? = null
) : Exception(message, cause)