package com.nbs.banking.core.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.serialization.KSerializer


data class ObjectPreferenceKey<T>(
    val key: Preferences.Key<String>,
    val serializer: KSerializer<T>
)

@JvmName("objectKey")
inline fun <reified T> objectPreferencesKey(
    name: String,
    serializer: KSerializer<T>
): ObjectPreferenceKey<T> =
    ObjectPreferenceKey(
        key = stringPreferencesKey(name),
        serializer = serializer
    )
