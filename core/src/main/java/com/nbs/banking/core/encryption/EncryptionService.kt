package com.nbs.banking.core.encryption

interface EncryptionService {
    fun encrypt(
        input: String,
        keyData: String,
        ivData: String,
    ): String

    fun decrypt(
        encryptedData: String,
        keyData: String,
        ivData: String,
    ): String
}
