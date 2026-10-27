package com.nbs.banking.core.notification

import com.nbs.banking.core.notification.NotificationKey.KEY_ACTION_PARAMETER
import com.nbs.banking.core.notification.NotificationKey.KEY_ACTION_TYPE
import com.google.firebase.messaging.RemoteMessage
import com.nbs.banking.core.extensions.nullIfAllPropertiesNull
import com.nbs.banking.core.model.CustomActions
import com.nbs.banking.core.model.NotificationData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun RemoteMessage.toNotificationData(json: Json): NotificationData? {
    val map = this.data
    if (map.isEmpty()) return null
    val notificationData = NotificationData(
        id = map["id"],
        topic = this.from ?: map["from"],
        body = map["body"],
        type = map["type"],
        image = map["image"],
        title = map["title"],
        actions = map["actions"],
        deeplink = map["deeplink"],
        customActions = map["customAction"],
        isShowNotification = map["isShowNotification"]?.toBooleanStrictOrNull() ?: true
    ).nullIfAllPropertiesNull()

    return notificationData?.copy(topic = this.from ?: notificationData.topic)
}

fun parseCustomActions(
    jsonString: String?,
    json: Json
): CustomActions? {
    if (jsonString.isNullOrEmpty()) return null
    return runCatching {
        val jsonElement = json.parseToJsonElement(jsonString)
        val jsonObject = jsonElement.jsonObject
        val actionType = jsonObject[KEY_ACTION_TYPE]?.jsonPrimitive?.content.orEmpty()
        val actionParameters = jsonObject[KEY_ACTION_PARAMETER]?.jsonObject?.mapValues {
            it.value.jsonPrimitive.content
        }.orEmpty()
        CustomActions(actionType, actionParameters)
    }.getOrNull()
}

object NotificationKey {
    const val KEY_ACTION_TYPE = "actionType"
    const val KEY_ACTION_PARAMETER = "actionParameters"
}
