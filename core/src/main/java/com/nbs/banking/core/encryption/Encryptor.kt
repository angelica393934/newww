package com.nbs.banking.core.encryption

import kotlinx.serialization.KSerializer

interface Encryptor {
    fun <T> encrypt(value: T, serializer: KSerializer<T>): String
    fun <T> decrypt(value: String, serializer: KSerializer<T>): T
}

interface EncryptionKeyManager {

    fun logDebugInfo()
    fun rotateKey()
}