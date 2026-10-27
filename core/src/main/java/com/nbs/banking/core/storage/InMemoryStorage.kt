package com.nbs.banking.core.storage

import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import timber.log.Timber

object InMemoryStorage {
    private val dataMap: MutableMap<String, Any> = ConcurrentHashMap()
    private val storageFlow = MutableSharedFlow<String>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    fun putString(key: String, value: String) {
        dataMap[key] = value
        storageFlow.tryEmit(key)
    }

    fun putInt(key: String, value: Int) {
        dataMap[key] = value
        storageFlow.tryEmit(key)
    }

    fun putBoolean(key: String, value: Boolean) {
        dataMap[key] = value
        storageFlow.tryEmit(key)
    }

    fun putFloat(key: String, value: Float) {
        dataMap[key] = value
        storageFlow.tryEmit(key)
    }

    fun putLong(key: String, value: Long) {
        dataMap[key] = value
        storageFlow.tryEmit(key)
    }

    inline fun <reified T> putDataObject(key: String, value: T) {
        putDataObject(key, serializer<T>(), value)
    }

    fun <T> putDataObject(key: String, serializer: KSerializer<T>, value: T, json: Json = Json) {
        val jsonString = json.encodeToString(serializer, value)
        dataMap[key] = jsonString
        storageFlow.tryEmit(key)
    }

    inline fun <reified T> getDataObject(key: String): T? =
        getDataObject(key, serializer<T>())

    fun <T> getDataObject(key: String, serializer: KSerializer<T>, json: Json = Json): T? {
        val jsonString = dataMap[key] as? String ?: return null
        return runCatching { json.decodeFromString(serializer, jsonString) }.getOrNull()
    }

    inline fun <reified T> getDataObjectFlow(key: String): Flow<T?> =
        getDataObjectFlow(key, serializer<T>())

    fun <T> getDataObjectFlow(key: String, serializer: KSerializer<T>, json: Json = Json): Flow<T?> = storageFlow
        .filter { it == key }
        .onStart { emit(key) }
        .map { getDataObject(key, serializer, json) }
        .distinctUntilChanged()

    // Retrieve data
    fun getString(key: String): String? = dataMap[key] as? String

    fun getInt(key: String): Int? = dataMap[key] as? Int

    fun getBoolean(key: String): Boolean? = dataMap[key] as? Boolean

    fun getFloat(key: String): Float? = dataMap[key] as? Float

    fun getLong(key: String): Long? = dataMap[key] as? Long

    fun remove(key: String) {
        dataMap.remove(key)
        storageFlow.tryEmit(key)
    }

    fun clear() {
        val keys = dataMap.keys.toList()
        dataMap.clear()
        keys.forEach { storageFlow.tryEmit(it) }
    }

    fun printData() {
        dataMap.forEach { (key, value) ->
            Timber.tag("InMemory").d("$key = $value")
        }
    }
}
