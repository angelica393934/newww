package com.nbs.banking.core.eventbus

import com.nbs.banking.core.extensions.throttleFirst
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance

object EventBus {
    private val _events = MutableSharedFlow<Any>()
    val events = _events.asSharedFlow()
    const val THROTTLE_TIME = 1000L

    suspend fun publish(event: Any) {
        _events.emit(event)
    }

    suspend inline fun <reified T> subscribe(crossinline onEvent: (T) -> Unit) {
        events
            .filterIsInstance<T>()
            .throttleFirst(THROTTLE_TIME)
            .collectLatest { event ->
                currentCoroutineContext().ensureActive()
                onEvent(event)
            }
    }
}
