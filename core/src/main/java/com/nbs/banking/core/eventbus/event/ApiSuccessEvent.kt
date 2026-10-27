package com.nbs.banking.core.eventbus.event

import com.nbs.banking.core.eventbus.EventBus
import com.nbs.banking.core.eventbus.EventBusHandler
import com.nbs.banking.core.extensions.emptyString

data class ApiSuccessEvent(val url: String = emptyString())

class ApiSuccessEventHandler : EventBusHandler<ApiSuccessEvent> {
    override suspend fun publish(data: ApiSuccessEvent) {
        EventBus.publish(data)
    }

    override suspend fun subscribe(onEventReceived: (ApiSuccessEvent) -> Unit) {
        EventBus.subscribe<ApiSuccessEvent> { pushEvent ->
            onEventReceived.invoke(pushEvent)
        }
    }
}
