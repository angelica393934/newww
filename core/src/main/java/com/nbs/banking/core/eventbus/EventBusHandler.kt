package com.nbs.banking.core.eventbus

import androidx.annotation.Keep

@Keep
interface EventBusHandler<T> {
    suspend fun publish(data: T)

    suspend fun subscribe(onEventReceived: (T) -> Unit)
}
