package com.nbs.banking.core.analytics.tools

interface AnalyticsProvider {
    val identifier: AnalyticsIdentifier

    fun setAnalyticsId(analyticsId: String)

    fun setDefaultProperties(properties: Map<String, Any?> = emptyMap())

    fun logEvent(event: String, params: Map<String, Any?> = emptyMap())

    fun logError(throwable: Throwable, params: Map<String, Any?> = emptyMap())
}
