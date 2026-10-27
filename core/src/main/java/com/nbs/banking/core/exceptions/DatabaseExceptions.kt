package com.nbs.banking.core.exceptions

import com.nbs.banking.core.error.AppException

open class DatabaseException(
    cause: Throwable,
    message: String? = null,
) : AppException(
    message = message ?: "Database operation failed",
    cause = cause,
)

class DatabaseConstraintException(
    cause: Throwable,
    message: String? = null,
) : DatabaseException(
    message = message ?: "Database constraint violation",
    cause = cause,
)

class DatabaseFullException(
    cause: Throwable,
    message: String? = null,
) : DatabaseException(
    message = message ?: "Database is full",
    cause = cause,
)