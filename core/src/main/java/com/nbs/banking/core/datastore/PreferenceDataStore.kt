package com.nbs.banking.core.datastore

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface PreferenceDataStore {

    fun <T> getEager(key: Preferences.Key<T>): T?
    fun <T> getEager(key: Preferences.Key<T>, default: T): T
    fun <T> getObjectEager(key: ObjectPreferenceKey<T>,): T?
    fun <T> getObjectEager(key: ObjectPreferenceKey<T>, default: T): T

    // suspend — with and without default
    suspend fun <T> get(key: Preferences.Key<T>): T?
    suspend fun <T> get(key: Preferences.Key<T>, default: T): T

    suspend fun <T> getObject(key: ObjectPreferenceKey<T>,): T?
    suspend fun <T> getObject(
        key: ObjectPreferenceKey<T>,
        default: T
    ): T

    // observe — with and without default
    fun <T> observe(key: Preferences.Key<T>, default: T): Flow<T>

    fun <T> observeObject(
        key: ObjectPreferenceKey<T>,
        default: T
    ): Flow<T>

    fun <T> observeObjectOrNull(
        key: ObjectPreferenceKey<T>,
    ): Flow<T?>

    fun <T> observeOrNull(key: Preferences.Key<T>): Flow<T?>

    // write
    suspend fun <T> save(key: Preferences.Key<T>, value: T)
    suspend fun <T> saveObject(
        key: ObjectPreferenceKey<T>,
        value: T,
    )

    suspend fun remove(vararg keys: Preferences.Key<*>)
    suspend fun clear()
}