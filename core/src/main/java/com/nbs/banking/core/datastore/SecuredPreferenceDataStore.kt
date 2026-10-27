package com.nbs.banking.core.datastore

import com.nbs.banking.core.encryption.EncryptedKey
import kotlinx.coroutines.flow.Flow

interface SecuredPreferenceDataStore{
    fun <T> getEager(key: EncryptedKey<T>): T?
    fun <T> getEager(key: EncryptedKey<T>, default: T): T
    suspend fun <T> get(key: EncryptedKey<T>): T?
    suspend fun <T> get(key: EncryptedKey<T>, default: T): T
    fun <T> observe(key: EncryptedKey<T>, default: T): Flow<T>
    fun <T> observeOrNull(key: EncryptedKey<T>): Flow<T?>
    suspend fun <T> save(key: EncryptedKey<T>, value: T)
    suspend fun remove(vararg keys: EncryptedKey<*>)
    suspend fun clear()
}