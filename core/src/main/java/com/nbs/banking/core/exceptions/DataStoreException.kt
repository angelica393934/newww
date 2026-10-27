package com.nbs.banking.core.exceptions

import com.nbs.banking.core.error.AppException

open class DataStoreException(
    cause: Throwable,
    message: String? = null,
) : AppException(cause, message ?: "DataStore operation failed")

class DataStoreReadException(
    cause: Throwable,
    message: String? = null,
) : DataStoreException(cause, message ?: "Failed to read from DataStore")

class DataStoreWriteException(
    cause: Throwable,
    message: String? = null,
) : DataStoreException(cause, message ?: "Failed to write to DataStore")

class DataStoreSerializationException(
    cause: Throwable,
    message: String? = null
) : DataStoreException(cause, message ?: "Failed to serialize/deserialize DataStore value")