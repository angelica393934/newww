package com.nbs.banking.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.nbs.banking.core.di.ApplicationScope
import com.nbs.banking.core.di.NormalDataStoreType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import javax.inject.Inject

class PreferenceDataStoreImpl @Inject constructor(
    @NormalDataStoreType private val dataStore: DataStore<Preferences>,
    private val json: Json,
    @ApplicationScope private val scope: CoroutineScope
) : PreferenceDataStore {

    private val preferencesState: StateFlow<Preferences> = dataStore.data
        .catchDataStoreExceptions()
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptyPreferences()
        )

    // ─── synchronous reads ────────────────────────────────────────────────
    // Use when the latest in-memory value is needed immediately.
    // Does not wait for DataStore to emit its first value.

    /**
     * Retrieves the latest value available in memory.
     * Returns `null` when the key is not present.
     *
     * Example:
     * ```kotlin
     * val username: String? = preferenceDataStore.getEager(
     *     UserSettingsKeys.USERNAME
     * )
     * ```
     *
     * Result:
     * `"JohnDoe"` when stored, or `null` when not set.
     */
    override fun <T> getEager(key: Preferences.Key<T>): T? =
        preferencesState.value[key]

    /**
     * Retrieves the latest value available in memory.
     * Returns [default] when the key is not present.
     *
     * Example:
     * ```kotlin
     * val username: String = preferenceDataStore.getEager(
     *     UserSettingsKeys.USERNAME,
     *     "Guest"
     * )
     * ```
     *
     * Result:
     * `"JohnDoe"` when stored, or `"Guest"` when not set.
     */
    override fun <T> getEager(key: Preferences.Key<T>, default: T): T =
        getEager(key) ?: default

    /**
     * Retrieves and deserializes the latest JSON value currently available
     * in memory.
     * Returns `null` when the key is not present.
     *
     * Example:
     * ```kotlin
     * val profile: UserProfile? = preferenceDataStore.getObjectEager(
     *     UserSettingsKeys.USER_PROFILE,
     *     UserProfile.serializer()
     * )
     * ```
     *
     * Result:
     * `UserProfile("John", "Admin")` when stored, or `null` when not set.
     */
    override fun <T> getObjectEager(
        key: ObjectPreferenceKey<T>
    ): T? = preferencesState.value[key.key]?.let {
        safeJsonDecode(key.serializer, json, it)
    }

    /**
     * Retrieves and deserializes the latest JSON value currently available
     * in memory.
     * Returns [default] when the key is not present.
     *
     * Example:
     * ```kotlin
     * val profile: UserProfile = preferenceDataStore.getObjectEager(
     *     UserSettingsKeys.USER_PROFILE,
     *     UserProfile.serializer(),
     *     UserProfile()
     * )
     * ```
     *
     * Result:
     * `UserProfile("John", "Admin")` when stored, or `UserProfile()`
     * when not set.
     */
    override fun <T> getObjectEager(
        key: ObjectPreferenceKey<T>,
        default: T
    ): T = getObjectEager(key) ?: default

    // ─── suspend reads ────────────────────────────────────────────────────
    // Use inside a coroutine when the current persisted value is needed once.

    /**
     * Retrieves and deserializes the latest JSON value currently available
     * in memory.
     * Returns [default] when the key is not present.
     *
     * Example:
     * ```kotlin
     * val profile: UserProfile = preferenceDataStore.getObjectEager(
     *     UserSettingsKeys.USER_PROFILE,
     *     UserProfile.serializer(),
     *     UserProfile()
     * )
     * ```
     *
     * Result:
     * `UserProfile("John", "Admin")` when stored, or `UserProfile()`
     * when not set.
     */
    override suspend fun <T> get(key: Preferences.Key<T>): T? =
        preferencesState.map { it[key] }.first()

    /**
     * Retrieves the current preference value as a one-shot read.
     * Returns [default] when the key is not present.
     *
     * Example:
     * ```kotlin
     * val timeout = preferenceDataStore.get(
     *     PreferencesKeys.TIMEOUT,
     *     30L
     * )
     * ```
     *
     * Result:
     * `60L` when stored, or `30L` when not set.
     */
    override suspend fun <T> get(key: Preferences.Key<T>, default: T): T =
        get(key) ?: default

    /**
     * Retrieves and deserializes the current JSON value as a one-shot read.
     * Returns `null` when the key is not present.
     *
     * Example:
     * ```kotlin
     * val session = preferenceDataStore.getObject(
     *     PreferencesKeys.SESSION,
     *     Session.serializer()
     * )
     * ```
     *
     * Result:
     * `Session(...)` when stored, or `null` when not set.
     */
    override suspend fun <T> getObject(
        key: ObjectPreferenceKey<T>,
    ): T? = preferencesState.map { prefs ->
        prefs[key.key]?.let {
            safeJsonDecode(key.serializer, json, it)
        }
    }.first()

    /**
     * Retrieves and deserializes the current JSON value as a one-shot read.
     * Returns [default] when the key is not present.
     *
     * Example:
     * ```kotlin
     * val session = preferenceDataStore.getObject(
     *     PreferencesKeys.SESSION,
     *     Session.serializer(),
     *     Session()
     * )
     * ```
     *
     * Result:
     * `Session(...)` when stored, or `Session()` when not set.
     */
    override suspend fun <T> getObject(
        key: ObjectPreferenceKey<T>,
        default: T
    ): T = getObject(key) ?: default

    // ─── reactive reads ───────────────────────────────────────────────────
    // Use when callers need to react to preference changes.

    /**
     * Observes a preference value as a [Flow].
     * Emits `null` when the key is not present.
     *
     * Example:
     * ```kotlin
     * preferenceDataStore
     *     .observeOrNull(UserSettingsKeys.USERNAME)
     *     .collect { username ->
     *         // ...
     *     }
     * ```
     *
     * Result:
     * Emits `"JohnDoe"`, followed by updated values or `null`.
     */
    override fun <T> observeOrNull(key: Preferences.Key<T>): Flow<T?> =
        preferencesState.map { it[key] }

    /**
     * Observes a preference value as a [Flow].
     * Emits [default] when the key is not present.
     *
     * Example:
     * ```kotlin
     * preferenceDataStore
     *     .observe(UserSettingsKeys.IS_DARK_MODE, false)
     *     .collect { isDarkMode ->
     *         // ...
     *     }
     * ```
     *
     * Result:
     * Emits `false`, `true`, `false`, etc. as the preference changes.
     */
    override fun <T> observe(key: Preferences.Key<T>, default: T): Flow<T> =
        observeOrNull(key).map { it ?: default }

    /**
     * Observes and deserializes a JSON object as a [Flow].
     * Emits `null` when the key is not present.
     *
     * Example:
     * ```kotlin
     * preferenceDataStore
     *     .observeObjectOrNull(
     *         UserSettingsKeys.USER_PROFILE,
     *         UserProfile.serializer()
     *     )
     *     .collect { profile ->
     *         // ...
     *     }
     * ```
     *
     * Result:
     * Emits `UserProfile(...)` when stored, or `null` when not set.
     */
    override fun <T> observeObjectOrNull(
        key: ObjectPreferenceKey<T>,
    ): Flow<T?> = preferencesState.map { prefs ->
        prefs[key.key]?.let { safeJsonDecode(key.serializer, json, it) }
    }

    /**
     * Observes and deserializes a JSON object as a [Flow].
     * Emits [default] when the key is not present.
     *
     * Example:
     * ```kotlin
     * preferenceDataStore
     *     .observeObject(
     *         UserSettingsKeys.USER_PROFILE,
     *         UserProfile.serializer(),
     *         UserProfile()
     *     )
     *     .collect { profile ->
     *         // ...
     *     }
     * ```
     *
     * Result:
     * Emits `UserProfile(...)` when stored, or `UserProfile()` when not set.
     */
    override fun <T> observeObject(
        key: ObjectPreferenceKey<T>,
        default: T
    ): Flow<T> = observeObjectOrNull(key).map { it ?: default }

    // ─── writes ───────────────────────────────────────────────────────────
    // DataStore writes are always suspend operations.

    /**
     * Saves a preference value to DataStore.
     * Example:
     * ```kotlin
     * preferenceDataStore.save(
     *     UserSettingsKeys.USERNAME,
     *     "JohnDoe"
     * )
     * ```
     *
     * Result:
     * The value `"JohnDoe"` is persisted under `USERNAME`.
     */
    override suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        safeDataStoreWrite {
            dataStore.edit { it[key] = value }
        }
    }

    /**
     * Serializes and saves an object as a JSON string in DataStore.
     * Example:
     * ```kotlin
     * preferenceDataStore.saveObject(
     *     UserSettingsKeys.USER_PROFILE,
     *     UserProfile.serializer(),
     *     UserProfile("Alice", "Admin")
     * )
     * ```
     *
     * Result:
     * The serialized `UserProfile` JSON is stored under `USER_PROFILE`.
     */
    override suspend fun <T> saveObject(
        key: ObjectPreferenceKey<T>,
        value: T
    ) {
        safeDataStoreWrite {
            dataStore.edit { prefs ->
                prefs[key.key] = safeJsonEncode(key.serializer, json, value)
            }
        }
    }

    /**
     * Removes one or more preference keys from DataStore.
     * Example:
     * ```kotlin
     * preferenceDataStore.remove(
     *     UserSettingsKeys.USERNAME,
     *     UserSettingsKeys.IS_DARK_MODE
     * )
     * ```
     *
     * Result:
     * The specified keys are removed from DataStore.
     */
    override suspend fun remove(vararg keys: Preferences.Key<*>) {
        safeDataStoreWrite {
            dataStore.edit { prefs ->
                keys.forEach { key -> prefs.remove(key) }
            }
        }
    }

    /**
     * Removes all preferences from DataStore.
     * Example:
     * ```kotlin
     * preferenceDataStore.clear()
     * ```
     *
     * Result:
     * All stored preferences are removed.
     */
    override suspend fun clear() {
        safeDataStoreWrite {
            dataStore.edit { it.clear() }
        }
    }
}