package com.nbs.banking.core.analytics

enum class EventValue(val key: String, val value: String?) {

}

fun EventValue.toKeyValuePair(): Pair<String, String?> = this.key to this.value
