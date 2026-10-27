package com.nbs.banking.core.encryption

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

class EncryptedKey<T> @PublishedApi internal constructor(
    internal val name: String,
    internal val serializer: KSerializer<T>
) {
    internal val preferenceKey: Preferences.Key<String>
        get() = stringPreferencesKey(name)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EncryptedKey<*>) return false
        return name == other.name
    }

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = "EncryptedKey(name=$name)"
}

inline fun <reified T> encryptedKey(name: String): EncryptedKey<T> =
    EncryptedKey(name, serializer())

fun <T> encryptedKey(name: String, serializer: KSerializer<T>): EncryptedKey<T> =
    EncryptedKey(name, serializer)