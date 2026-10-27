package com.nbs.banking.core.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

fun <T> Flow<T>.throttleFirst(windowMillis: Long): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect { value ->
        val now = System.currentTimeMillis()
        if (now - lastEmissionTime >= windowMillis) {
            lastEmissionTime = now
            emit(value)
        }
    }
}

suspend fun <T> withIo(block: suspend () -> T): T = withContext(Dispatchers.IO) { block() }

suspend fun <T> withDefault(block: suspend () -> T): T =
    withContext(Dispatchers.Default) { block() }

suspend fun <T> withMain(block: suspend () -> T): T = withContext(Dispatchers.Main) { block() }

inline fun <T, R> Flow<T>.mapOnIo(crossinline transform: suspend (T) -> R): Flow<R> =
    this.map { withContext(Dispatchers.IO) { transform(it) } }

inline fun <T, R> Flow<T>.mapOnDefault(crossinline transform: suspend (T) -> R): Flow<R> =
    this.map { withContext(Dispatchers.Default) { transform(it) } }

inline fun <T, R> Flow<T>.mapOnMain(crossinline transform: suspend (T) -> R): Flow<R> =
    this.map { withContext(Dispatchers.Main) { transform(it) } }