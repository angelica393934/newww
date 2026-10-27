package com.nbs.banking.core.utils

import android.webkit.JavascriptInterface
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class GamificationJsInterface(
    private val onAnalyticsEvent: (String, Map<String, Any?>) -> Unit,
    private val onOpenDeeplink: (deeplink: String) -> Unit,
) {
    @JavascriptInterface
    fun openDeeplink(json: String) {
        try {
            val deeplink = JSONObject(json).getString("deeplinkUrl")

            Timber.d("deeplinkPath: $deeplink")
            onOpenDeeplink(deeplink)
        } catch (error: JSONException) {
            Timber.d(error, "Error processing postMessage")
        }
    }

    @JavascriptInterface
    fun postMessage(json: String) {
        try {
            val jsonObject = JSONObject(json)

            val eventName = jsonObject.getString("eventName")
            val parametersJson = jsonObject.getString("parameters")
            val parameters = JSONObject(parametersJson)

            val paramMap = mutableMapOf<String, String>()
            parameters.keys().forEach { key ->
                paramMap[key] = parameters.getString(key)
            }

            Timber.tag("GamificationJsInterface")
                .d("postMessage: $eventName with params: $paramMap")
            onAnalyticsEvent.invoke(eventName, paramMap)
        } catch (error: JSONException) {
            Timber.tag("GamificationJsInterface").d(error, "Error processing postMessage")
        }
    }
}
