package com.nbs.banking.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.nbs.banking.core.di.ApplicationScope
import com.nbs.banking.core.di.SecuredPreferenceDataStoreType
import com.nbs.banking.core.encryption.EncryptedKey
import com.nbs.banking.core.encryption.Encryptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class SecuredPreferenceDataStoreImpl @Inject constructor(
    @SecuredPreferenceDataStoreType private val dataStore: DataStore<Preferences>,
    private val encryptor: Encryptor,
    @ApplicationScope private val scope: CoroutineScope
) : SecuredPreferenceDataStore {

    private val preferencesState: StateFlow<Preferences> = dataStore.data
        .catchDataStoreExceptions()
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptyPreferences()
        )

    // ─── Synchronous reads ────────────────────────────────────────────────
    // Use when the latest in-memory value is needed immediately.
    // Does not wait for DataStore to emit its first value.

    /**
     * Retrieves and decrypts the latest value currently available in memory.
     * Returns `null` when the key is not present.
     *
     * Example:
     * ```kotlin
     * val token: String? = securedPreferenceDataStore.getEager(
     *     SecuredKeys.AUTH_TOKEN
     * )
     * ```
     *
     * Result:
     * `"Bearer token_xyz"` when stored, or `null` when not set.
     */
    override fun <T> getEager(key: EncryptedKey<T>): T? =
        preferencesState.value[key.preferenceKey]?.let {
            encryptor.decrypt(it, key.serializer)
        }

    /**
     * Retrieves and decrypts the latest value currently available in memory.
     * Returns [default] when the key is not present.
     *
     * Example:
     * ```kotlin
     * val token: String = securedPreferenceDataStore.getEager(
     *     SecuredKeys.AUTH_TOKEN,
     *     ""
     * )
     * ```
     *
     * Result:
     * `"Bearer token_xyz"` when stored, or `""` when not set.
     */
    override fun <T> getEager(key: EncryptedKey<T>, default: T): T = getEager(key) ?: default

    // ─── One-shot reads ───────────────────────────────────────────────────
    // Use inside a coroutine when the current persisted value is needed once.

    /**
     * Retrieves and decrypts the current value as a one-shot read.
     * Returns `null` when the key is not present.
     *
     * Example:
     * ```kotlin
     * val token = securedPreferenceDataStore.get(
     *     SecuredKeys.AUTH_TOKEN
     * )
     * ```
     *
     * Result:
     * `"Bearer token_xyz"` when stored, or `null` when not set.
     */
    override suspend fun <T> get(key: EncryptedKey<T>): T? =
        preferencesState.map { prefs ->
            prefs[key.preferenceKey]?.let {
                encryptor.decrypt(it, key.serializer)
            }
        }.first()

    /**
     * Retrieves and decrypts the current value as a one-shot read.
     * Returns [default] when the key is not present.
     *
     * Example:
     * ```kotlin
     * val token = securedPreferenceDataStore.get(
     *     SecuredKeys.AUTH_TOKEN,
     *     ""
     * )
     * ```
     *
     * Result:
     * `"Bearer token_xyz"` when stored, or `""` when not set.
     */
    override suspend fun <T> get(
        key: EncryptedKey<T>,
        default: T
    ): T = get(key) ?: default


    /**
     * Observes and decrypts a secured preference value as a [Flow].
     * Emits `null` when the key is not present.
     *
     * Example:
     * ```kotlin
     * securedPreferenceDataStore
     *     .observeOrNull(SecuredKeys.AUTH_TOKEN)
     *     .collect { token ->
     *         // ...
     *     }
     * ```
     *
     * Result:
     * Emits `"Bearer token_xyz"`, followed by updated values or `null`.
     */
    override fun <T> observeOrNull(key: EncryptedKey<T>): Flow<T?> =
        preferencesState.map { prefs ->
            prefs[key.preferenceKey]?.let {
                encryptor.decrypt(it, key.serializer)
            }

        }

    /**
     * Observes and decrypts a secured preference value as a [Flow].
     * Emits [default] when the key is not present.
     *
     * Example:
     * ```kotlin
     * securedPreferenceDataStore
     *     .observe(SecuredKeys.IS_LOGGED_IN, false)
     *     .collect { isLoggedIn ->
     *         // ...
     *     }
     * ```
     *
     * Result:
     * Emits `false`, `true`, `false`, etc. as the preference changes.
     */
    override fun <T> observe(
        key: EncryptedKey<T>,
        default: T
    ): Flow<T> = observeOrNull(key).map { it ?: default }


    // ─── Writes ───────────────────────────────────────────────────────────
    // Values are encrypted before being written to DataStore.
    // DataStore writes are always suspend operations.

    /**
     * Encrypts and saves a value to DataStore.
     *
     * Example:
     * ```kotlin
     * securedPreferenceDataStore.save(
     *     SecuredKeys.AUTH_TOKEN,
     *     "Bearer token_xyz"
     * )
     * ```
     *
     * Result:
     * The encrypted value is stored under `AUTH_TOKEN`.
     */
    override suspend fun <T> save(key: EncryptedKey<T>, value: T) {
        safeDataStoreWrite {
            dataStore.edit { prefs ->
                prefs[key.preferenceKey] = encryptor.encrypt(value, key.serializer)
            }
        }
    }

    /**
     * Removes one or more secured preference keys from DataStore.
     * Example:
     * ```kotlin
     * securedPreferenceDataStore.remove(
     *     SecuredKeys.AUTH_TOKEN,
     *     SecuredKeys.USER_ID
     * )
     * ```
     *
     * Result:
     * The specified secured preferences are removed from DataStore.
     */
    override suspend fun remove(vararg keys: EncryptedKey<*>) {
        safeDataStoreWrite {
            dataStore.edit { prefs ->
                keys.forEach { prefs.remove(it.preferenceKey) }
            }
        }
    }

    /**
     * Removes all secured preferences from DataStore.
     *
     * Example:
     * ```kotlin
     * securedPreferenceDataStore.clear()
     * ```
     *
     * Result:
     * All secured preferences are removed.
     */
    override suspend fun clear() {
        safeDataStoreWrite {
            dataStore.edit { it.clear() }
        }
    }
}