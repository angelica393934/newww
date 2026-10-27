package com.nbs.banking.core.analytics

import android.security.keystore.KeyProperties.DIGEST_SHA256
import androidx.compose.runtime.staticCompositionLocalOf
import com.nbs.banking.core.analytics.tools.AnalyticsIdentifier
import com.nbs.banking.core.analytics.tools.AnalyticsProvider
import com.nbs.banking.core.extensions.emptyString
import timber.log.Timber
import java.security.MessageDigest
import javax.inject.Inject

class Analytics @Inject constructor(
    private val analyticsProviders: List<AnalyticsProvider>,
) {
    companion object {
        private const val BYTE_TO_HEX = "%02x"
        private const val DROP_LENGTH = 10
        private const val HASH_LENGTH = 16

        fun generateAnalyticsId(input: String?): String {
            if (input.isNullOrBlank()) return emptyString()

            val digest = MessageDigest.getInstance(DIGEST_SHA256)
            digest.update("$input-saltkey".toByteArray())
            val hashedBytes = digest.digest()
            val analyticsId = hashedBytes.joinToString("") { String.format(BYTE_TO_HEX, it) }.drop(DROP_LENGTH).take(HASH_LENGTH)
            Timber.d("generateAnalyticsId: $analyticsId")
            return analyticsId
        }
    }

    // ref for logging so it doesn't have to query to UserStorage everytime
    private var analyticsIdRef = ""

    fun initAnalyticsSession() {
//        setAnalyticsId(userStorage.getAnalyticsId())
        setDefaultProperties(generateDefaultProperties())
        Timber.d("Analytics Session Initialized")
    }

    private fun setAnalyticsId(analyticsId: String) {
        analyticsProviders.forEach {
            it.setAnalyticsId(analyticsId)
        }
        analyticsIdRef = analyticsId
        Timber.d("Analytics Id: $analyticsIdRef")
    }

    private fun setDefaultProperties(defaultProperties: Map<String, Any?> = emptyMap()) {
        analyticsProviders.forEach {
            it.setDefaultProperties(defaultProperties)
        }
        Timber.d("Default Properties: $defaultProperties")
    }

    private fun generateDefaultProperties() = mutableMapOf<String, Any>().apply {

    }

    fun trackScreen(screen: String?, vararg providers: AnalyticsIdentifier = arrayOf(AnalyticsIdentifier.FIREBASE_ANALYTICS)) {

        val params = mutableMapOf<String, Any>().apply {
//            put(FirebaseAnalytics.Param.SCREEN_NAME, screenName.orEmpty())
//            put(AnalyticProperties.ROUTE_NAME, routeName.orEmpty())
            appendDateParams()
        }

        analyticsProviders
            .filter { provider -> providers.any { provider.identifier == it } }
            .forEach {
//                it.logEvent(eventType.eventName, params)
            }
    }

    fun trackEvent(
        event: EventType,
        params: Map<String, Any?>,
        vararg providers: AnalyticsIdentifier = arrayOf(AnalyticsIdentifier.FIREBASE_ANALYTICS, AnalyticsIdentifier.CRASHLYTICS),
    ) {
        val updatedParams = params.appendDateParams()
        analyticsProviders.filter { provider -> providers.any { provider.identifier == it } }
            .forEach {
                it.logEvent(event.eventName, updatedParams)
            }
    }

    fun trackEvent(
        event: String,
        params: Map<String, Any?>,
        vararg providers: AnalyticsIdentifier = AnalyticsIdentifier.entries.toTypedArray(),
    ) {
        val updatedParams = params.appendDateParams()
        analyticsProviders.filter { provider -> providers.any { provider.identifier == it } }
            .forEach {
                it.logEvent(event, updatedParams)
            }
    }

    fun trackNonFatalException(
        throwable: Throwable,
        params: Map<String, Any?> = emptyMap(),
        vararg providers: AnalyticsIdentifier = arrayOf(AnalyticsIdentifier.CRASHLYTICS),
    ) {
        val updatedParams = params.appendDateParams()
        analyticsProviders.filter { provider -> providers.any { provider.identifier == it } }
            .forEach {
                it.logError(throwable, updatedParams)
            }
    }

    private fun Map<String, Any?>.appendDateParams(): Map<String, Any?> = this.toMutableMap().apply {
//        put(AnalyticProperties.DATE, formatToCustomUtc(System.currentTimeMillis()))
    }
}

val LocalAnalytics = staticCompositionLocalOf<Analytics> { error("No Analytics provided") }
