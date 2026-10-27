package com.nbs.banking.core.exceptions

import com.nbs.banking.core.error.AppException

open class EncryptionException(
    cause: Throwable,
    message: String? = null,
) : AppException(cause, message ?: "Encryption operation failed")

class EncryptDataException(
    cause: Throwable,
    message: String? = null
) : EncryptionException(cause, message ?: "Failed to encrypt data")

class DecryptDataException(
    cause: Throwable,
    message: String? = null
) : EncryptionException(cause, message ?: "Failed to decrypt data")