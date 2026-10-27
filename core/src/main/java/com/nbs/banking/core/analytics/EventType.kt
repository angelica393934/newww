package com.nbs.banking.core.analytics

import kotlinx.serialization.Serializable

@Serializable
enum class EventType(val eventName: String) {
    EVENT("event_name"),
    DEEPLINK("deeplink");

    override fun toString(): String = eventName
}
