package com.nbs.banking.core.analytics.tools

import com.google.firebase.crashlytics.CustomKeysAndValues
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class CrashlyticsProvider @Inject constructor(
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val json: Json,
) : AnalyticsProvider {
    override val identifier: AnalyticsIdentifier = AnalyticsIdentifier.CRASHLYTICS

    override fun setAnalyticsId(analyticsId: String) {
        firebaseCrashlytics.setUserId(analyticsId)
    }

    override fun setDefaultProperties(properties: Map<String, Any?>) {
        val propertiesBuilder = CustomKeysAndValues.Builder()
        properties.forEach {
            when (it.value) {
                is String -> propertiesBuilder.putString(it.key, it.value as String)
                is Int -> propertiesBuilder.putInt(it.key, it.value as Int)
                is Double -> propertiesBuilder.putDouble(it.key, it.value as Double)
                is Long -> propertiesBuilder.putLong(it.key, it.value as Long)
                is Float -> propertiesBuilder.putFloat(it.key, it.value as Float)
                is Boolean -> propertiesBuilder.putBoolean(it.key, it.value as Boolean)
                else -> propertiesBuilder.putString(it.key, it.value.toString())
            }
        }
        firebaseCrashlytics.setCustomKeys(propertiesBuilder.build())
    }

    override fun logEvent(event: String, params: Map<String, Any?>) {
        val paramsString = runCatching {
            val jsonObject = buildJsonObject {
                params.forEach { (k, v) ->
                    put(k, JsonPrimitive(v?.toString() ?: ""))
                }
            }
            json.encodeToString(JsonObject.serializer(), jsonObject)
        }.getOrNull() ?: params.toString()
        firebaseCrashlytics.log("$event params: $paramsString")
    }

    override fun logError(throwable: Throwable, params: Map<String, Any?>) {
        if (params.isNotEmpty()) {
            logEvent(throwable::class.java.canonicalName.orEmpty() + throwable.message, params)
        }
        firebaseCrashlytics.recordException(throwable)
    }
}
