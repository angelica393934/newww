package com.nbs.banking.core.datastore

import com.nbs.banking.core.exceptions.DataStoreSerializationException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

fun <T> safeJsonDecode(
    serializer: KSerializer<T>,
    json: Json,
    raw: String
): T = try {
    json.decodeFromString(serializer, raw)
} catch (e: SerializationException) {
    throw DataStoreSerializationException(e, e.message.orEmpty())
}

fun <T> safeJsonEncode(
    serializer: KSerializer<T>,
    json: Json,
    value: T
): String = try {
    json.encodeToString(serializer, value)
} catch (e: SerializationException) {
    throw DataStoreSerializationException(e, e.message.orEmpty())
}