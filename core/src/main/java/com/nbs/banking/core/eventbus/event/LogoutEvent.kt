package com.nbs.banking.core.eventbus.event

import com.nbs.banking.core.eventbus.EventBus
import com.nbs.banking.core.eventbus.EventBusHandler

data object LogoutEvent

class LogoutEventHandler : EventBusHandler<LogoutEvent> {
    override suspend fun publish(data: LogoutEvent) {
        EventBus.publish(data)
    }

    override suspend fun subscribe(onEventReceived: (LogoutEvent) -> Unit) {
        EventBus.subscribe<LogoutEvent> { pushEvent ->
            onEventReceived.invoke(pushEvent)
        }
    }
}
