package com.nbs.banking.core.eventbus.event

import com.nbs.banking.core.eventbus.EventBus
import com.nbs.banking.core.eventbus.EventBusHandler

sealed class ApiErrorEvent(val errorCode: String) {

}

class ApiErrorEventHandler : EventBusHandler<ApiErrorEvent> {
    override suspend fun publish(data: ApiErrorEvent) {
        EventBus.publish(data)
    }

    override suspend fun subscribe(onEventReceived: (ApiErrorEvent) -> Unit) {
        EventBus.subscribe<ApiErrorEvent> { silentPushEvent ->
            onEventReceived.invoke(silentPushEvent)
        }
    }
}
