package com.nbs.banking.core.eventbus.event

import com.nbs.banking.core.eventbus.EventBus
import com.nbs.banking.core.eventbus.EventBusHandler
import com.nbs.banking.core.extensions.emptyString

data class SilentPushEvent(
    val message: String,
    val deeplink: String?,
    val notificationData: NotificationData = NotificationData(),
)

data class NotificationData(
    val title: String = emptyString(),
    val desc: String = emptyString(),
    val type: String = emptyString(),
)

class SilentPushEventHandler : EventBusHandler<SilentPushEvent> {
    override suspend fun publish(data: SilentPushEvent) {
        EventBus.publish(data)
    }

    override suspend fun subscribe(onEventReceived: (SilentPushEvent) -> Unit) {
        EventBus.subscribe<SilentPushEvent> { silentPushEvent ->
            onEventReceived.invoke(silentPushEvent)
        }
    }
}

enum class SilentPushAction(val action: String)
